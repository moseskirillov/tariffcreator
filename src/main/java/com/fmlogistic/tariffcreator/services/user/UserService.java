package com.fmlogistic.tariffcreator.services.user;

import com.fmlogistic.tariffcreator.models.user.request.LoginRequest;
import com.fmlogistic.tariffcreator.models.user.request.RefreshRequest;
import com.fmlogistic.tariffcreator.models.user.request.RegisterRequest;
import com.fmlogistic.tariffcreator.models.user.request.ResetRequest;
import com.fmlogistic.tariffcreator.models.user.request.UpdateRequest;
import com.fmlogistic.tariffcreator.models.user.response.LoginResponse;
import com.fmlogistic.tariffcreator.models.user.response.RefreshResponse;
import com.fmlogistic.tariffcreator.models.user.response.RegisterResponse;

public interface UserService {
    RegisterResponse registration(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    RefreshResponse refreshToken(RefreshRequest request);
    void resetPassword(ResetRequest request);
    void changePassword(String token);
    String updatePassword(UpdateRequest request);
}
