package com.team5.web_ide.domain.comment.service;

import com.team5.web_ide.domain.comment.dto.CommentCreateRequest;
import com.team5.web_ide.domain.comment.dto.CommentResponse;
import com.team5.web_ide.domain.comment.dto.CommentUpdateRequest;
import com.team5.web_ide.domain.comment.entity.Comment;
import com.team5.web_ide.domain.comment.exception.CommentErrorCode;
import com.team5.web_ide.domain.comment.exception.CommentException;
import com.team5.web_ide.domain.comment.repository.CommentRepository;
import com.team5.web_ide.domain.member.entity.ProjectMember;
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

    @Transactional
    public CommentResponse createComment(Long projectId, CommentCreateRequest request) {
        Project project = projectService.findActiveProject(projectId);
        User writer = findActiveUser(request.getWriterId());
        validateProjectMember(projectId, writer.getId());

        Comment comment = commentRepository.save(Comment.builder()
                .project(project)
                .writer(writer)
                .filePath(normalizeFilePath(request.getFilePath()))
                .lineNumber(request.getLineNumber())
                .content(normalizeContent(request.getContent()))
                .build());

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getComments(Long projectId, Long requesterId, String filePath) {
        projectService.findActiveProject(projectId);
        validateProjectMember(projectId, requesterId);

        String normalizedFilePath = normalizeFilePath(filePath);
        List<Comment> comments = normalizedFilePath == null
                ? commentRepository.findAllByProjectIdAndStatusOrderByCreatedAtAsc(
                        projectId,
                        Comment.CommentStatus.ACTIVE
                )
                : commentRepository.findAllByProjectIdAndFilePathAndStatusOrderByCreatedAtAsc(
                        projectId,
                        normalizedFilePath,
                        Comment.CommentStatus.ACTIVE
                );

        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(Long projectId, Long commentId, CommentUpdateRequest request) {
        projectService.findActiveProject(projectId);
        Comment comment = findActiveComment(projectId, commentId);
        validateWriterOrOwner(projectId, comment, request.getRequesterId());

        comment.updateContent(normalizeContent(request.getContent()));
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long projectId, Long commentId, Long requesterId) {
        projectService.findActiveProject(projectId);
        Comment comment = findActiveComment(projectId, commentId);
        validateWriterOrOwner(projectId, comment, requesterId);
        comment.delete();
    }

    private Comment findActiveComment(Long projectId, Long commentId) {
        return commentRepository.findByIdAndProjectIdAndStatus(
                        commentId,
                        projectId,
                        Comment.CommentStatus.ACTIVE
                )
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateWriterOrOwner(Long projectId, Comment comment, Long requesterId) {
        ProjectMember member = validateProjectMember(projectId, requesterId);
        boolean writer = comment.getWriter().getId().equals(requesterId);
        boolean owner = member.getRole() == ProjectMember.ProjectRole.OWNER;
        if (!writer && !owner) {
            throw new CommentException(CommentErrorCode.COMMENT_ACCESS_DENIED);
        }
    }

    private ProjectMember validateProjectMember(Long projectId, Long userId) {
        if (userId == null) {
            throw new CommentException(CommentErrorCode.COMMENT_ACCESS_DENIED);
        }
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_ACCESS_DENIED));
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new CommentException(CommentErrorCode.USER_STATUS_BLOCKED);
        }
        return user;
    }

    private String normalizeContent(String content) {
        String normalized = content == null ? "" : content.trim();
        if (normalized.isEmpty()) {
            throw new CommentException(CommentErrorCode.COMMENT_CONTENT_EMPTY);
        }
        return normalized;
    }

    private String normalizeFilePath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return null;
        }
        return filePath.trim();
    }
}
