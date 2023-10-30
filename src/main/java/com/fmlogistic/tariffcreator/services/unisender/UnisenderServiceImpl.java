package com.fmlogistic.tariffcreator.services.unisender;

import com.fmlogistic.tariffcreator.exceptions.MailjetException;
import com.fmlogistic.tariffcreator.models.minio.RejectRequest;
import com.fmlogistic.tariffcreator.models.unisender.Body;
import com.fmlogistic.tariffcreator.models.unisender.Message;
import com.fmlogistic.tariffcreator.models.unisender.Recipient;
import com.fmlogistic.tariffcreator.models.unisender.SendRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnisenderServiceImpl implements UnisenderService {

    private static final String SUBJECT_REJECT_MESSAGE_USER_ERROR = "Просьба доработать файл %s по комментариям от тарифного специалиста";
    private static final String TEXT_PART_REJECTION_MESSAGE = "Файл отклонен тарифным специалистом \"%s\" с комментарием: \"%s\"\n" + "За дополнительными объяснениями можно обратиться к вышеуказанному специалисту";
    private static final String TEXT_PART_CREATION_MESSAGE = "Пользователем %s создан(-ы) файлы в форме %s по клиенту %s";
    private static final String SUBJECT_CREATION_MESSAGE = "Новые созданные тарифные файлы %s для прогрузки в BlyJay от %s по клиенту %s по датам от %s до %s";
    private static final String MANUAL_LOADING_FILE_SUBJECT = "Ручная прогрузка %s";
    private static final String RESET_PASSWORD_SUBJECT = "Сброс пароля";
    private static final String GENERATE_TARIFF_ERROR_SUBJECT = "Ошибка генерации файла %s";
    private static final String MANUAL_LOADING_FILE_BODY = "Файл будет прогружен тарифным специалистом %s";
    private static final String GENERATE_TARIFF_ERROR_BODY = "Генерация файла для клиента %s по форме %s завершена с ошибкой. Для подробностей обратитесь к техническому специалисту";

    private static final String UNISENDER_API_URL = "https://go2.unisender.ru/ru/transactional/api/v1/email/send.json";
    private static final String BR_HTML_TAG = "<br />";
    private static final String DOWNLOAD_FILE_LINK_TAG = "<a href=\"%s\" download>%s</a>";
    private static final String DOWNLOAD_FILE_LINK_METHOD = "https://k8s-ru.fmlogistic.com/lsc-team/tariff-creator/api/files/downloadFile?bucketName=%s&fileName=%s";
    private static final String BUCKET_STORAGE_NAME = "tarifffiles";
    private static final String EMAIL_SEND_ERROR = "Email send error:";

    private static final String ONE = "1";
    private static final String TWO = "2";

    private static final String FROM_EMAIL = "ru-ipi-developers@fmlogistic.com";
    private static final String TO_EMAIL = "ru-tra-tariff@fmlogistic.com";
    private static final String FROM_NAME = "Tariff Creator";

    private static final String CREATION_MESSAGE_REQUEST = "Получен запрос на отправку письма о создании файла";
    private static final String REJECT_MESSAGE_REQUEST = "Получен запрос на отправку письма об отклонении файла";
    private static final String RESET_PASSWORD_MESSAGE_REQUEST = "Получен запрос на отправку письма о сбросе пароля";
    private static final String ERROR_MESSAGE_REQUEST = "Получен запрос на отправку письма об ошибке генерации файла";
    private static final String MAIL_SEND_SUCCESS = "Письмо успешно отправлено";
    private static final String FILE_TITLE = "<b>Файл: </b>";
    private static final String COMMENT_TITLE = "<b>Коментрий: %s</b>";

    @Value("${unisender.api.key}")
    private String UNISENDER_API_KEY;

    private final RestTemplate restTemplate;

    @Override
    public void sendCreationMessage(String userName, String formName, String clientName, List<String> filesNames, String dateFrom, String dateTo, String comment) {
        try {
            log.info(CREATION_MESSAGE_REQUEST);
            sendMessage(createSendRequest(
                formName,
                userName,
                clientName,
                dateFrom,
                dateTo,
                createMailBody(userName, formName, clientName, filesNames, comment)
            ));
            log.info(MAIL_SEND_SUCCESS);
        } catch (Exception e) {
            log.error(EMAIL_SEND_ERROR, e);
            throw e;
        }
    }

    private SendRequest createSendRequest(
        String formName,
        String userName,
        String clientName,
        String dateFrom,
        String dateTo,
        String body
    ) {
        return new SendRequest(
            UNISENDER_API_KEY, new Message(
            FROM_EMAIL,
            FROM_NAME,
            String.format(SUBJECT_CREATION_MESSAGE, formName, userName, clientName, dateFrom, dateTo),
            new Body(body),
            List.of(new Recipient(TO_EMAIL), new Recipient(userName)))
        );
    }

    @Override
    public void sendRejectMessage(RejectRequest request) {
        log.info(REJECT_MESSAGE_REQUEST);
        try {
            if (request.getKind().equals(ONE)) {
                sendMessage(new SendRequest(
                    UNISENDER_API_KEY,
                    new Message(
                        FROM_EMAIL,
                        FROM_NAME,
                        String.format(SUBJECT_REJECT_MESSAGE_USER_ERROR, request.getFile()),
                        new Body(String.format(TEXT_PART_REJECTION_MESSAGE, request.getEmail(), request.getReason())),
                        List.of(new Recipient(TO_EMAIL), new Recipient(request.getEmail()))
                    )
                ));
                log.info(MAIL_SEND_SUCCESS);
            } else if (request.getKind().equals(TWO)) {
                sendMessage(new SendRequest(
                    UNISENDER_API_KEY,
                    new Message(
                        FROM_EMAIL,
                        FROM_NAME,
                        String.format(MANUAL_LOADING_FILE_SUBJECT, request.getFile()),
                        new Body(String.format(MANUAL_LOADING_FILE_BODY, request.getEmail())),
                        List.of(new Recipient(TO_EMAIL))))
                );
                log.info(MAIL_SEND_SUCCESS);
            }
        } catch (Exception e) {
            log.error(EMAIL_SEND_ERROR, e);
            throw new MailjetException(e.getMessage());
        }
    }

    @Override
    public void sendResetPasswordMessage(String link, String email) {
        log.info(RESET_PASSWORD_MESSAGE_REQUEST);
        try {
            sendMessage(new SendRequest(
                UNISENDER_API_KEY,
                new Message(
                    FROM_EMAIL,
                    FROM_NAME,
                    RESET_PASSWORD_SUBJECT,
                    new Body(link),
                    List.of(new Recipient(email)))
            ));
            log.info(MAIL_SEND_SUCCESS);
        } catch (Exception e) {
            log.error(EMAIL_SEND_ERROR, e);
            throw new MailjetException(e.getMessage());
        }
    }

    @Override
    public void sendErrorMessage(String email, String clientName, String type) {
        log.info(ERROR_MESSAGE_REQUEST);
        try {
            sendMessage(new SendRequest(
                UNISENDER_API_KEY,
                new Message(
                    FROM_EMAIL,
                    FROM_NAME,
                    String.format(GENERATE_TARIFF_ERROR_SUBJECT, type),
                    new Body(String.format(GENERATE_TARIFF_ERROR_BODY, clientName, type)),
                    List.of(new Recipient(email)))
            ));
            log.info(MAIL_SEND_SUCCESS);
        } catch (Exception e) {
            log.error(EMAIL_SEND_ERROR, e);
            throw new MailjetException(e.getMessage());
        }
    }

    private String createMailBody(String userName, String formName, String clientName, List<String> filesNames, String comment) {
        var body = new StringBuilder();
        body.append(String.format(TEXT_PART_CREATION_MESSAGE, userName, formName, clientName));
        body.append(BR_HTML_TAG);
        body.append(BR_HTML_TAG);
        for (var fileName : filesNames) {
            body.append(FILE_TITLE);
            body.append(String.format(DOWNLOAD_FILE_LINK_TAG, String.format(DOWNLOAD_FILE_LINK_METHOD, BUCKET_STORAGE_NAME, fileName), fileName));
            body.append(BR_HTML_TAG);
        }
        if (StringUtils.isNoneBlank(comment)) {
            body.append(BR_HTML_TAG);
            body.append(String.format(COMMENT_TITLE, comment));
            body.append(BR_HTML_TAG);
        }
        return body.toString();
    }

    @SneakyThrows
    private void sendMessage(SendRequest request) {
        var uri = new URI(UNISENDER_API_URL);
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        var entity = new HttpEntity<>(request, headers);
        var result = restTemplate.postForEntity(uri, entity, String.class);
        log.info(result.toString());
    }
}
