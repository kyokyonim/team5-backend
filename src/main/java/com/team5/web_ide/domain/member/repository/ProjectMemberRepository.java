package com.team5.web_ide.domain.member.repository;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findAllByProjectIdOrderByIdAsc(Long projectId);

    List<ProjectMember> findAllByUserId(Long userId);
}
