package com.team5.web_ide.domain.comment.repository;

import com.team5.web_ide.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndProjectIdAndStatus(
            Long id,
            Long projectId,
            Comment.CommentStatus status
    );

    List<Comment> findAllByProjectIdAndStatusOrderByCreatedAtAsc(
            Long projectId,
            Comment.CommentStatus status
    );

    List<Comment> findAllByProjectIdAndFilePathAndStatusOrderByCreatedAtAsc(
            Long projectId,
            String filePath,
            Comment.CommentStatus status
    );
}
