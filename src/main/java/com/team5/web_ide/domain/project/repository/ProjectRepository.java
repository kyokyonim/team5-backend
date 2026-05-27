package com.team5.web_ide.domain.project.repository;

import com.team5.web_ide.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>{

}
