package com.fmlogistic.tariffcreator.repositories;

import com.fmlogistic.tariffcreator.entities.CommentFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentFilesRepository extends JpaRepository<CommentFiles, Long> {
    Optional<CommentFiles> findByFilename(String fileName);
}
