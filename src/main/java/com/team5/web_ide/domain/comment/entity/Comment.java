package com.team5.web_ide.domain.comment.entity;

import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(length = 1000)
    private String filePath;

    private Integer lineNumber;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommentStatus status = CommentStatus.ACTIVE;

    @Builder
    public Comment(Project project, User writer, String filePath, Integer lineNumber, String content) {
        this.project = project;
        this.writer = writer;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.content = content;
        this.status = CommentStatus.ACTIVE;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.status = CommentStatus.DELETED;
    }

    public enum CommentStatus {
        ACTIVE,
        DELETED
    }
}
