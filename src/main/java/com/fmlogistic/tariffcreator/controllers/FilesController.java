package com.fmlogistic.tariffcreator.controllers;

import com.fmlogistic.tariffcreator.models.minio.FilesModel;
import com.fmlogistic.tariffcreator.models.minio.MoveFileRequest;
import com.fmlogistic.tariffcreator.models.minio.RejectRequest;
import com.fmlogistic.tariffcreator.services.minio.MinioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Tag(name = "Для работы с Minio")
public class FilesController {

    private static final String ATTACHMENT_HEADER = "attachment";
    private static final String CACHE_HEADER = "must-revalidate, post-check=0, pre-check=0";

    private final MinioService minioService;

    @GetMapping("/download")
    public ResponseEntity<FilesModel> download(@RequestParam String bucketName) {
        return ResponseEntity.ok(minioService.downloadFiles(bucketName));
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String bucketName, @RequestParam String fileName) {
        return new ResponseEntity<>(minioService.downloadFile(bucketName, fileName), headers(fileName), HttpStatus.OK);
    }

    @PostMapping("/move")
    public ResponseEntity<Void> moveFile(@RequestBody MoveFileRequest request) {
        minioService.moveToAnotherBucket(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> rejectFile(@RequestBody RejectRequest request) {
        minioService.rejectFile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private HttpHeaders headers(String fileName) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setCacheControl(CACHE_HEADER);
        headers.setContentDispositionFormData(ATTACHMENT_HEADER, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        return headers;
    }
}
