package com.team5.web_ide.domain.member.repository;


import com.team5.web_ide.domain.member.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // 이 유저가 이미 이 프로젝트 멤버인지 확(멤버 중복 추가 방지)
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
    // 특정 유저가 특정 프로젝트에서 어떤 role인지 조회
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
    //프로젝트 상세페이지에서 멤버 목록 보여줄 때 사용
    List<ProjectMember> findAllByProjectId(Long projectId);
}
