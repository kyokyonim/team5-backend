package com.team5.web_ide.domain.invite.repository;

import com.team5.web_ide.domain.invite.entity.ProjectInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Long> {

    Optional<ProjectInvite> findByToken(String token);

    boolean existsByProjectIdAndInviteeEmailAndStatus(
            Long projectId,
            String inviteeEmail,
            ProjectInvite.InviteStatus status
    );

    List<ProjectInvite> findAllByProjectIdAndStatusOrderByCreatedAtDesc(
            Long projectId,
            ProjectInvite.InviteStatus status
    );
}
