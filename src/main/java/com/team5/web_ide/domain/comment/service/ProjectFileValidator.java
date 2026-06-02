package com.team5.web_ide.domain.comment.service;

import com.team5.web_ide.domain.comment.exception.CommentErrorCode;
import com.team5.web_ide.domain.comment.exception.CommentException;
import com.team5.web_ide.domain.file.entity.ProjectFile;
import com.team5.web_ide.domain.file.repository.ProjectFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectFileValidator {

    private final ProjectFileRepository projectFileRepository;

    public void validateFileExists(Long projectId, Long fileId) {
        if (fileId == null) {
            throw new CommentException(CommentErrorCode.FILE_NOT_FOUND);
        }

        ProjectFile file = projectFileRepository.findByIdAndProjectId(fileId, projectId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.FILE_NOT_FOUND));

        if (!file.isFile()) {
            throw new CommentException(CommentErrorCode.FILE_NOT_FOUND);
        }
    }
}
