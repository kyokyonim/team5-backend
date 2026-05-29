package com.team5.web_ide.domain.comment.service;

import com.team5.web_ide.domain.comment.dto.CommentCreateRequest;
import com.team5.web_ide.domain.comment.dto.CommentResponse;
import com.team5.web_ide.domain.comment.dto.CommentUpdateRequest;
import com.team5.web_ide.domain.comment.entity.Comment;
import com.team5.web_ide.domain.comment.exception.CommentErrorCode;
import com.team5.web_ide.domain.comment.exception.CommentException;
import com.team5.web_ide.domain.comment.repository.CommentRepository;
import com.team5.web_ide.domain.member.repository.ProjectMemberRepository;
import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.project.service.ProjectService;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectService projectService;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectFileValidator projectFileValidator;

    public List<CommentResponse> getComments(
            Long projectId,
            Long fileId,
            Boolean resolved,
            Long userId
    ) {
        projectService.findActiveProject(projectId);
        findActiveUser(userId);
        validateProjectMember(projectId, userId);
        projectFileValidator.validateFileExists(projectId, fileId);

        List<Comment> comments = resolved == null
                ? commentRepository.findAllByProjectIdAndFileIdAndDeletedAtIsNullOrderByLineNumberAscCreatedAtAsc(
                        projectId,
                        fileId
                )
                : commentRepository.findAllByProjectIdAndFileIdAndResolvedAndDeletedAtIsNullOrderByLineNumberAscCreatedAtAsc(
                        projectId,
                        fileId,
                        resolved
                );

        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse createComment(
            Long projectId,
            Long fileId,
            Long userId,
            CommentCreateRequest request
    ) {
        Project project = projectService.findActiveProject(projectId);
        User writer = findActiveUser(userId);
        validateProjectMember(projectId, userId);
        projectFileValidator.validateFileExists(projectId, fileId);

        Comment comment = commentRepository.save(Comment.builder()
                .project(project)
                .fileId(fileId)
                .writer(writer)
                .lineNumber(validateLineNumber(request))
                .content(validateContent(request == null ? null : request.getContent()))
                .build());

        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse updateComment(
            Long projectId,
            Long fileId,
            Long commentId,
            Long userId,
            CommentUpdateRequest request
    ) {
        projectService.findActiveProject(projectId);
        findActiveUser(userId);
        validateProjectMember(projectId, userId);

        Comment comment = findActiveComment(projectId, fileId, commentId);
        validateCommentWriter(comment, userId, CommentErrorCode.COMMENT_UPDATE_DENIED);

        comment.updateContent(validateContent(request == null ? null : request.getContent()));
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long projectId, Long fileId, Long commentId, Long userId) {
        projectService.findActiveProject(projectId);
        findActiveUser(userId);
        validateProjectMember(projectId, userId);

        Comment comment = findActiveComment(projectId, fileId, commentId);
        validateCommentWriter(comment, userId, CommentErrorCode.COMMENT_DELETE_DENIED);
        comment.delete();
    }

    private Comment findActiveComment(Long projectId, Long fileId, Long commentId) {
        return commentRepository.findByIdAndProjectIdAndFileIdAndDeletedAtIsNull(
                        commentId,
                        projectId,
                        fileId
                )
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateProjectMember(Long projectId, Long userId) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new CommentException(CommentErrorCode.PROJECT_ACCESS_DENIED);
        }
    }

    private void validateCommentWriter(
            Comment comment,
            Long userId,
            CommentErrorCode errorCode
    ) {
        if (!comment.isWriter(userId)) {
            throw new CommentException(errorCode);
        }
    }

    private User findActiveUser(Long userId) {
        if (userId == null) {
            throw new CommentException(CommentErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.UNAUTHORIZED));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new CommentException(CommentErrorCode.PROJECT_ACCESS_DENIED);
        }

        return user;
    }

    private Integer validateLineNumber(CommentCreateRequest request) {
        Integer lineNumber = request == null ? null : request.getLineNumber();
        if (lineNumber == null || lineNumber <= 0) {
            throw new CommentException(CommentErrorCode.INVALID_LINE_NUMBER);
        }
        return lineNumber;
    }

    private String validateContent(String content) {
        String normalized = content == null ? "" : content.trim();
        if (normalized.isEmpty()) {
            throw new CommentException(CommentErrorCode.COMMENT_CONTENT_EMPTY);
        }
        if (normalized.length() > 1000) {
            throw new CommentException(CommentErrorCode.COMMENT_CONTENT_TOO_LONG);
        }
        return normalized;
    }
}
