package com.fmlogistic.tariffcreator.services.minio;

import com.fmlogistic.tariffcreator.exceptions.RejectFileException;
import com.fmlogistic.tariffcreator.models.minio.FileModel;
import com.fmlogistic.tariffcreator.models.minio.FilesModel;
import com.fmlogistic.tariffcreator.models.minio.MoveFileRequest;
import com.fmlogistic.tariffcreator.models.minio.RejectRequest;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CommentFilesService;
import com.fmlogistic.tariffcreator.services.unisender.UnisenderService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String TARIFF_CREATED_BUCKET_NAME = "tariffcreated";
    private static final String TARIFF_REJECTED_BUCKET_NAME = "tariffrejected";
    private static final String THREE = "3";
    private static final String XML_CONTENT_TYPE = "application/xml";

    private static final String MOVE_FILE_TO_ANOTHER_BUCKET_ERROR = "Произошла ошибка при переносе файла в другой бакет:";

    private final MinioClient minioClient;
    private final UnisenderService unisenderService;
    private final CommentFilesService commentFilesService;

    @Override
    public FilesModel downloadFiles(String bucketName) {
        return new FilesModel(downloadFilesFromBucket(bucketName));
    }

    @Override
    public void moveToAnotherBucket(MoveFileRequest request) {
        try {
            minioClient
                    .copyObject(CopyObjectArgs
                            .builder()
                            .bucket(request.getToBucketName())
                            .object(request.getFileName())
                            .source(CopySource
                                    .builder()
                                    .bucket(request.getFromBucketName())
                                    .object(request.getFileName())
                                    .build())
                            .build());
            minioClient
                    .removeObject(RemoveObjectArgs
                            .builder()
                            .bucket(request.getFromBucketName())
                            .object(request.getFileName())
                            .build());
        } catch (Exception e) {
            log.error(MOVE_FILE_TO_ANOTHER_BUCKET_ERROR, e);
        }
    }

    @Override
    @SneakyThrows
    public byte[] downloadFile(String bucketName, String fileName) {
        try (var stream = minioClient
                .getObject(GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build())) {
            return stream.readAllBytes();
        }
    }

    @Override
    public void rejectFile(RejectRequest request) {
        try {
            moveToAnotherBucket(new MoveFileRequest(
                    request.getFile(), TARIFF_CREATED_BUCKET_NAME, TARIFF_REJECTED_BUCKET_NAME)
            );
            if (!request.getKind().equals(THREE)) {
                unisenderService.sendRejectMessage(request);
            }
        } catch (RuntimeException e) {
            throw new RejectFileException(e.getMessage());
        }
    }

    @SneakyThrows
    public void uploadFile(String bucketName, File file) {
        minioClient.uploadObject(
                UploadObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(file.getName())
                        .filename(file.getPath())
                        .contentType(XML_CONTENT_TYPE)
                        .build()
        );
    }

    @SneakyThrows
    private List<FileModel> downloadFilesFromBucket(String bucket) {
        var items = StreamSupport
                .stream(minioClient.listObjects(ListObjectsArgs
                        .builder()
                        .bucket(bucket)
                        .build()).spliterator(), true)
                .toList();
        var files = new ArrayList<FileModel>();
        for (var item : items) {
            var file = item.get();
            var comment = commentFilesService.findByFileName(file.objectName());
            files.add(
                    new FileModel(
                            file.objectName(),
                            comment.commentFile(),
                            file.lastModified().toString(),
                            DateTimeFormatter.ofPattern(DATE_PATTERN).format(file.lastModified())
                    )
            );
        }
        return files;
    }
}