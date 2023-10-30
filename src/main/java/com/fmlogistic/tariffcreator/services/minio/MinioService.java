package com.fmlogistic.tariffcreator.services.minio;

import com.fmlogistic.tariffcreator.models.minio.FilesModel;
import com.fmlogistic.tariffcreator.models.minio.MoveFileRequest;
import com.fmlogistic.tariffcreator.models.minio.RejectRequest;

import java.io.File;

public interface MinioService {
    FilesModel downloadFiles(String bucketName);
    void moveToAnotherBucket(MoveFileRequest request);
    byte[] downloadFile(String bucketName, String fileName);
    void rejectFile(RejectRequest request);
    void uploadFile(String bucketName, File file);
}
