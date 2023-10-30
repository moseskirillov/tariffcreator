package com.fmlogistic.tariffcreator.models.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileModel {
    private String name;
    private String comment;
    private String fullDate;
    private String date;
}
