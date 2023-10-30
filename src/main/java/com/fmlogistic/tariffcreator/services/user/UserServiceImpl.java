package com.fmlogistic.tariffcreator.services.user;

import com.fmlogistic.tariffcreator.entities.PasswordReset;
import com.fmlogistic.tariffcreator.entities.Role;
import com.fmlogistic.tariffcreator.entities.User;
import com.fmlogistic.tariffcreator.exceptions.RefreshException;
import com.fmlogistic.tariffcreator.exceptions.RegisterException;
import com.fmlogistic.tariffcreator.exceptions.ResetPasswordTokenNotValid;
import com.fmlogistic.tariffcreator.jwt.JwtProvider;
import com.fmlogistic.tariffcreator.models.user.AccessTokenModel;
import com.fmlogistic.tariffcreator.models.user.request.LoginRequest;
import com.fmlogistic.tariffcreator.models.user.request.RefreshRequest;
import com.fmlogistic.tariffcreator.models.user.request.RegisterRequest;
import com.fmlogistic.tariffcreator.models.user.request.ResetRequest;
import com.fmlogistic.tariffcreator.models.user.request.UpdateRequest;
import com.fmlogistic.tariffcreator.models.user.response.LoginResponse;
import com.fmlogistic.tariffcreator.models.user.response.RefreshResponse;
import com.fmlogistic.tariffcreator.models.user.response.RegisterResponse;
import com.fmlogistic.tariffcreator.repositories.PasswordResetRepository;
import com.fmlogistic.tariffcreator.repositories.RoleRepository;
import com.fmlogistic.tariffcreator.repositories.UserRepository;
import com.fmlogistic.tariffcreator.services.unisender.UnisenderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UnisenderService unisenderService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetRepository passwordResetRepository;

    @Override
    @Transactional
    public RegisterResponse registration(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.email())) {
                throw new RegisterException(RegisterResponse.DUPLICATE);
            }
            userRepository.save(userFromRequest(request));
            return RegisterResponse.SUCCESS;
        } catch (RegisterException e) {
            log.error("Ошибка данных для регистрации: {}, {}", request.email(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            var userCandidate = userRepository.findByEmail(request.email());
            if (userCandidate.isPresent()) {
                var authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return new LoginResponse(
                    userCandidate.get().getEmail(),
                    userCandidate.get()
                        .getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()),
                    jwtProvider.generateAccessToken(entityToAccessTokenModel(userCandidate.get())),
                    jwtProvider.generateRefreshToken(request.email())
                );
            }
            throw new UsernameNotFoundException("Пользовтель не найден");
        } catch (UsernameNotFoundException e) {
            log.error("Ошибка при входе под логином {}: {}", request.email(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public RefreshResponse refreshToken(RefreshRequest request) {
        try {
            if (jwtProvider.validateRefreshToken(request.refreshToken())) {
                var email = jwtProvider.getRefreshClaims(request.refreshToken()).getSubject();
                var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
                return new RefreshResponse(
                    email,
                    user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()),
                    jwtProvider.generateAccessToken(entityToAccessTokenModel(user)),
                    jwtProvider.generateRefreshToken(email)
                );
            }
            throw new RefreshException("Невалидный refresh token");
        } catch (RuntimeException e) {
            throw new RefreshException(e);
        }
    }

    @Override
    public void resetPassword(ResetRequest request) {
        var user = userRepository.findByEmail(request.email());
        if (user.isPresent()) {
            var token = UUID.randomUUID().toString();
            createPasswordResetToken(user.get(), token);
            unisenderService.sendResetPasswordMessage("https://k8s-ru.fmlogistic.com/lsc-team/tariff-creator/changePassword?token=" + token, request.email());
        } else {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void changePassword(String token) {
        var result = passwordResetRepository.findByToken(token);
        if (result.isEmpty()) {
            throw new ResetPasswordTokenNotValid();
        }
    }

    @Override
    public String updatePassword(UpdateRequest request) {
        var token = passwordResetRepository.findByToken(request.token());
        if (token.isPresent()) {
            var user = token.get().getUser();
            user.setPassword(passwordEncoder.encode(request.password()));
            userRepository.save(user);
        }
        return "OK";
    }

    private User userFromRequest(RegisterRequest request) {
        var role = roleRepository.findByName("ROLE_USER");
        var user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(false);
        user.setRoles(Collections.singletonList(role));
        return user;
    }

    private AccessTokenModel entityToAccessTokenModel(User user) {
        var roles = user.getRoles().stream()
            .map(Role::getName)
            .toList();
        return new AccessTokenModel(user.getEmail(), user.getFirstName(), user.getLastName(), roles);
    }

    private void createPasswordResetToken(User user, String token) {
        passwordResetRepository.save(new PasswordReset(token, user, LocalDateTime.now().plusMinutes(5)));
    }
}
