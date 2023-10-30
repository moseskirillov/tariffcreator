package com.fmlogistic.tariffcreator.services.unisender;

import com.fmlogistic.tariffcreator.models.minio.RejectRequest;

import java.util.List;

public interface UnisenderService {
    void sendCreationMessage(String userName, String formName, String clientName, List<String> files, String dateFrom, String dateTo, String comment);
    void sendRejectMessage(RejectRequest request);
    void sendResetPasswordMessage(String link, String email);
    void sendErrorMessage(String email, String clientName, String type);
}
