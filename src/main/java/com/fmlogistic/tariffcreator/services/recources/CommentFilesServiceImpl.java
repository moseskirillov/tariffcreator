package com.fmlogistic.tariffcreator.services.recources;

import com.fmlogistic.tariffcreator.entities.CommentFiles;
import com.fmlogistic.tariffcreator.models.resources.CommentFilesModel;
import com.fmlogistic.tariffcreator.repositories.CommentFilesRepository;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CommentFilesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentFilesServiceImpl implements CommentFilesService {

    private final CommentFilesRepository commentFilesRepository;

    @Override
    public CommentFilesModel findByFileName(String fileName) {
        var entity = commentFilesRepository.findByFilename(fileName);
        return entity
                .map(commentFiles -> new CommentFilesModel(commentFiles.getComment()))
                .orElseGet(() -> new CommentFilesModel(StringUtils.EMPTY));
    }

    @Override
    public void saveOrUpdate(String fileName, String commentFile) {
        var comment = commentFilesRepository.findByFilename(fileName);
        if (comment.isPresent()) {
            comment.get().setComment(commentFile);
            commentFilesRepository.save(comment.get());
        } else {
            commentFilesRepository.save(new CommentFiles(fileName, commentFile));
        }
    }
}
