package com.fmlogistic.tariffcreator.services.recources.interfaces;

import com.fmlogistic.tariffcreator.models.resources.CommentFilesModel;

public interface CommentFilesService {
    CommentFilesModel findByFileName(String fileName);
    void saveOrUpdate(String fileName, String commentFile);
}
