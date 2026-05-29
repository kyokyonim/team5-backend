package com.team5.web_ide.domain.comment.repository;

import com.team5.web_ide.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndProjectIdAndFileIdAndDeletedAtIsNull(
            Long id,
            Long projectId,
            Long fileId
    );

    List<Comment> findAllByProjectIdAndFileIdAndDeletedAtIsNullOrderByLineNumberAscCreatedAtAsc(
            Long projectId,
            Long fileId
    );

    List<Comment> findAllByProjectIdAndFileIdAndResolvedAndDeletedAtIsNullOrderByLineNumberAscCreatedAtAsc(
            Long projectId,
            Long fileId,
            boolean resolved
    );
}
