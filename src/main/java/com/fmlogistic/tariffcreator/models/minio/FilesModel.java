package com.fmlogistic.tariffcreator.models.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FilesModel {
    private List<FileModel> files;
}
