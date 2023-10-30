package com.fmlogistic.tariffcreator.models.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MoveFileRequest {
    private String fileName;
    private String fromBucketName;
    private String toBucketName;
}
