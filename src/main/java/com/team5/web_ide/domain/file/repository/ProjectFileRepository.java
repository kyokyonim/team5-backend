package com.team5.web_ide.domain.file.repository;

import com.team5.web_ide.domain.file.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {

    List<ProjectFile> findByProjectId(Long projectId);

    List<ProjectFile> findByProjectIdAndParentId(Long projectId, Long parentId);

    List<ProjectFile> findByProjectIdAndParentIdIsNull(Long projectId);

    Optional<ProjectFile> findByIdAndProjectId(Long id, Long projectId);

    boolean existsByProjectIdAndParentIdAndName(
            Long projectId,
            Long parentId,
            String name
    );

    boolean existsByProjectIdAndParentIdIsNullAndName(
            Long projectId,
            String name
    );

    boolean existsByProjectIdAndParentIdAndNameAndIdNot(
            Long projectId,
            Long parentId,
            String name,
            Long id
    );

    boolean existsByProjectIdAndParentIdIsNullAndNameAndIdNot(
            Long projectId,
            String name,
            Long id
    );

    List<ProjectFile> findByProjectIdAndPathStartingWith(
            Long projectId,
            String pathPrefix
    );

    boolean existsByProjectIdAndPath(
            Long projectId,
            String path
    );
}