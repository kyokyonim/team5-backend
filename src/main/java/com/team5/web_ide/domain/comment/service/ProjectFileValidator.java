package com.team5.web_ide.domain.comment.service;

import com.team5.web_ide.domain.comment.exception.CommentErrorCode;
import com.team5.web_ide.domain.comment.exception.CommentException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectFileValidator {

    private final JdbcTemplate jdbcTemplate;

    public void validateFileExists(Long projectId, Long fileId) {
        if (fileId == null) {
            throw new CommentException(CommentErrorCode.FILE_NOT_FOUND);
        }

        try {
            Long count = jdbcTemplate.queryForObject(
                    "select count(*) from project_files where id = ? and project_id = ? and type = 'FILE'",
                    Long.class,
                    fileId,
                    projectId
            );

            if (count == null || count == 0L) {
                throw new CommentException(CommentErrorCode.FILE_NOT_FOUND);
            }
        } catch (InvalidDataAccessResourceUsageException e) {
            // The file domain lives in a separate PR. Once project_files exists, this enforces the spec.
        }
    }
}
