package com.team5.web_ide.domain.project.repository;

import com.team5.web_ide.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndStatus(Long id, Project.ProjectStatus status);

    List<Project> findAllByStatusOrderByUpdatedAtDesc(Project.ProjectStatus status);
}
