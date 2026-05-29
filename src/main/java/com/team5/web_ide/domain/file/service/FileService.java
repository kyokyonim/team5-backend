package com.team5.web_ide.domain.file.service;

import com.team5.web_ide.domain.file.dto.*;
import com.team5.web_ide.domain.file.entity.FileType;
import com.team5.web_ide.domain.file.entity.ProjectFile;
import com.team5.web_ide.domain.file.exception.FileErrorCode;
import com.team5.web_ide.domain.file.exception.FileException;
import com.team5.web_ide.domain.file.repository.ProjectFileRepository;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final ProjectFileRepository projectFileRepository;
    private final FileLockService fileLockService;
    private final UserRepository userRepository;

    public List<FileTreeResponse> getFileTree(Long projectId, Long userId) {
        validateProjectReadable(projectId, userId);

        List<ProjectFile> files = projectFileRepository.findByProjectId(projectId);

        Map<Long, FileTreeResponse> nodeMap = files.stream()
                .collect(Collectors.toMap(
                        ProjectFile::getId,
                        FileTreeResponse::from
                ));

        List<FileTreeResponse> roots = new ArrayList<>();

        for (ProjectFile file : files) {
            FileTreeResponse node = nodeMap.get(file.getId());

            if (file.getParentId() == null) {
                roots.add(node);
                continue;
            }

            FileTreeResponse parent = nodeMap.get(file.getParentId());

            if (parent != null) {
                parent.addChild(node);
            }
        }

        return roots;
    }

    @Transactional
    public FileCreateResponse createFile(
            Long projectId,
            Long userId,
            FileCreateRequest request
    ) {
        validateProjectWritable(projectId, userId);
        validateFileName(request.getName());
        validateDuplicateName(projectId, request.getParentId(), request.getName());

        ProjectFile parent = getParentFolderIfExists(projectId, request.getParentId());
        String path = buildPath(parent, request.getName());

        ProjectFile file = ProjectFile.builder()
                .projectId(projectId)
                .parentId(request.getParentId())
                .name(request.getName())
                .type(FileType.FILE)
                .path(path)
                .content(request.getContent() == null ? "" : request.getContent())
                .language(request.getLanguage())
                .build();

        ProjectFile savedFile = projectFileRepository.save(file);

        return FileCreateResponse.from(savedFile);
    }

    @Transactional
    public FileCreateResponse createFolder(
            Long projectId,
            Long userId,
            FolderCreateRequest request
    ) {
        validateProjectWritable(projectId, userId);
        validateFileName(request.getName());
        validateDuplicateName(projectId, request.getParentId(), request.getName());

        ProjectFile parent = getParentFolderIfExists(projectId, request.getParentId());
        String path = buildPath(parent, request.getName());

        ProjectFile folder = ProjectFile.builder()
                .projectId(projectId)
                .parentId(request.getParentId())
                .name(request.getName())
                .type(FileType.FOLDER)
                .path(path)
                .content(null)
                .language(null)
                .build();

        ProjectFile savedFolder = projectFileRepository.save(folder);

        return FileCreateResponse.from(savedFolder);
    }

    public FileDetailResponse getFileDetail(
            Long projectId,
            Long fileId,
            Long userId
    ) {
        validateProjectReadable(projectId, userId);

        ProjectFile file = getProjectFile(projectId, fileId);

        if (!file.isFile()) {
            throw new FileException(FileErrorCode.NOT_FILE);
        }

        return FileDetailResponse.from(file);
    }

    @Transactional
    public FileContentUpdateResponse updateFileContent(
            Long projectId,
            Long fileId,
            Long userId,
            FileContentUpdateRequest request
    ) {
        validateProjectWritable(projectId, userId);

        ProjectFile file = getProjectFile(projectId, fileId);

        if (!file.isFile()) {
            throw new FileException(FileErrorCode.NOT_FILE);
        }

        fileLockService.validateLockOwner(projectId, fileId, userId);

        if (!file.getVersion().equals(request.getVersion())) {
            throw new FileException(FileErrorCode.FILE_VERSION_CONFLICT);
        }

        file.updateContent(request.getContent());

        fileLockService.unlock(projectId, fileId);

        return FileContentUpdateResponse.from(file);
    }

    @Transactional
    public FileRenameResponse renameFile(
            Long projectId,
            Long fileId,
            Long userId,
            FileRenameRequest request
    ) {
        validateProjectWritable(projectId, userId);
        validateFileName(request.getName());

        ProjectFile file = getProjectFile(projectId, fileId);

        validateDuplicateNameForRename(
                projectId,
                file.getParentId(),
                request.getName(),
                file.getId()
        );

        if (file.isFile()) {
            fileLockService.validateNotLockedByOther(projectId, fileId, userId);
        }

        if (file.isFolder()) {
            validateFolderChildrenNotLocked(projectId, file, userId);
        }

        String oldPath = file.getPath();
        String newPath = buildRenamedPath(file, request.getName());

        file.rename(request.getName(), newPath);

        if (file.isFolder()) {
            updateChildrenPath(projectId, oldPath, newPath);
        }

        return FileRenameResponse.from(file);
    }

    @Transactional
    public void deleteFile(
            Long projectId,
            Long fileId,
            Long userId
    ) {
        validateProjectWritable(projectId, userId);

        ProjectFile file = getProjectFile(projectId, fileId);

        if (file.isFile()) {
            fileLockService.validateNotLockedByOther(projectId, fileId, userId);
            fileLockService.unlock(projectId, fileId);
            projectFileRepository.delete(file);
            return;
        }

        validateFolderChildrenNotLocked(projectId, file, userId);

        List<ProjectFile> children = getFolderChildren(projectId, file);
        projectFileRepository.deleteAll(children);
        projectFileRepository.delete(file);
    }

    public FileNameValidateResponse validateName(
            Long projectId,
            Long userId,
            FileNameValidateRequest request
    ) {
        validateProjectWritable(projectId, userId);

        if (isInvalidFileName(request.getName())) {
            return FileNameValidateResponse.invalid("사용할 수 없는 파일 또는 폴더명입니다.");
        }

        boolean duplicated;

        if (request.getExcludeId() == null) {
            duplicated = existsSameName(projectId, request.getParentId(), request.getName());
        } else {
            duplicated = existsSameNameExceptSelf(
                    projectId,
                    request.getParentId(),
                    request.getName(),
                    request.getExcludeId()
            );
        }

        if (duplicated) {
            return FileNameValidateResponse.invalid("같은 위치에 동일한 이름이 이미 존재합니다.");
        }

        return FileNameValidateResponse.valid();
    }

    public FileLockResponse lockFile(
            Long projectId,
            Long fileId,
            Long userId
    ) {
        validateProjectWritable(projectId, userId);

        ProjectFile file = getProjectFile(projectId, fileId);

        if (!file.isFile()) {
            throw new FileException(FileErrorCode.NOT_FILE);
        }

        String nickname = getCurrentUserNickname(userId);

        FileLockInfo lockInfo = fileLockService.lock(
                projectId,
                fileId,
                userId,
                nickname
        );

        return FileLockResponse.from(lockInfo, userId);
    }

    public boolean existsFile(Long projectId, Long fileId) {
        return projectFileRepository.findByIdAndProjectId(fileId, projectId)
                .filter(ProjectFile::isFile)
                .isPresent();
    }

    public ProjectFile getFile(Long projectId, Long fileId) {
        ProjectFile file = getProjectFile(projectId, fileId);

        if (!file.isFile()) {
            throw new FileException(FileErrorCode.NOT_FILE);
        }

        return file;
    }

    private ProjectFile getProjectFile(Long projectId, Long fileId) {
        return projectFileRepository.findByIdAndProjectId(fileId, projectId)
                .orElseThrow(() -> new FileException(FileErrorCode.FILE_NOT_FOUND));
    }

    private ProjectFile getParentFolderIfExists(Long projectId, Long parentId) {
        if (parentId == null) {
            return null;
        }

        ProjectFile parent = projectFileRepository.findByIdAndProjectId(parentId, projectId)
                .orElseThrow(() -> new FileException(FileErrorCode.PARENT_FOLDER_NOT_FOUND));

        if (!parent.isFolder()) {
            throw new FileException(FileErrorCode.PARENT_NOT_FOLDER);
        }

        return parent;
    }

    private void validateDuplicateName(Long projectId, Long parentId, String name) {
        if (existsSameName(projectId, parentId, name)) {
            throw new FileException(FileErrorCode.DUPLICATE_FILE_NAME);
        }
    }

    private void validateDuplicateNameForRename(
            Long projectId,
            Long parentId,
            String name,
            Long fileId
    ) {
        if (existsSameNameExceptSelf(projectId, parentId, name, fileId)) {
            throw new FileException(FileErrorCode.DUPLICATE_FILE_NAME);
        }
    }

    private boolean existsSameName(Long projectId, Long parentId, String name) {
        if (parentId == null) {
            return projectFileRepository.existsByProjectIdAndParentIdIsNullAndName(
                    projectId,
                    name
            );
        }

        return projectFileRepository.existsByProjectIdAndParentIdAndName(
                projectId,
                parentId,
                name
        );
    }

    private boolean existsSameNameExceptSelf(
            Long projectId,
            Long parentId,
            String name,
            Long fileId
    ) {
        if (parentId == null) {
            return projectFileRepository.existsByProjectIdAndParentIdIsNullAndNameAndIdNot(
                    projectId,
                    name,
                    fileId
            );
        }

        return projectFileRepository.existsByProjectIdAndParentIdAndNameAndIdNot(
                projectId,
                parentId,
                name,
                fileId
        );
    }

    private void validateFileName(String name) {
        if (isInvalidFileName(name)) {
            throw new FileException(FileErrorCode.INVALID_FILE_NAME);
        }
    }

    private boolean isInvalidFileName(String name) {
        if (name == null || name.isBlank()) {
            return true;
        }

        return name.contains("/")
                || name.contains("\\")
                || name.equals(".")
                || name.equals("..");
    }

    private String buildPath(ProjectFile parent, String name) {
        if (parent == null) {
            return "/" + name;
        }

        return parent.getPath() + "/" + name;
    }

    private String buildRenamedPath(ProjectFile file, String newName) {
        Long parentId = file.getParentId();

        if (parentId == null) {
            return "/" + newName;
        }

        String currentPath = file.getPath();
        int lastSlashIndex = currentPath.lastIndexOf("/");

        String parentPath = currentPath.substring(0, lastSlashIndex);

        return parentPath + "/" + newName;
    }

    private List<ProjectFile> getFolderChildren(Long projectId, ProjectFile folder) {
        String pathPrefix = folder.getPath() + "/";

        return projectFileRepository.findByProjectIdAndPathStartingWith(
                projectId,
                pathPrefix
        );
    }

    private void updateChildrenPath(Long projectId, String oldPath, String newPath) {
        String oldPathPrefix = oldPath + "/";

        List<ProjectFile> children = projectFileRepository.findByProjectIdAndPathStartingWith(
                projectId,
                oldPathPrefix
        );

        for (ProjectFile child : children) {
            String childNewPath = newPath + child.getPath().substring(oldPath.length());
            child.updatePath(childNewPath);
        }
    }

    private void validateFolderChildrenNotLocked(
            Long projectId,
            ProjectFile folder,
            Long userId
    ) {
        List<ProjectFile> children = getFolderChildren(projectId, folder);

        List<Long> childFileIds = children.stream()
                .filter(ProjectFile::isFile)
                .map(ProjectFile::getId)
                .toList();

        if (fileLockService.hasLockedFile(projectId, childFileIds)) {
            throw new FileException(FileErrorCode.FILE_LOCKED);
        }
    }

    private User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new FileException(FileErrorCode.USER_STATUS_BLOCKED);
        }

        return user;
    }

    private void validateProjectReadable(Long projectId, Long userId) {
        getActiveUser(userId);

        // TODO: Project/Member 도메인 코드가 올라오면 구현
        // 1. Project 존재 여부 확인
        // 2. ProjectMember에서 projectId + userId 조회
        // 3. OWNER, EDITOR, VIEWER이면 허용
    }

    private void validateProjectWritable(Long projectId, Long userId) {
        getActiveUser(userId);

        // TODO: Project/Member 도메인 코드가 올라오면 구현
        // 1. Project 존재 여부 확인
        // 2. ProjectMember에서 projectId + userId 조회
        // 3. OWNER, EDITOR만 허용
        // 4. VIEWER면 FILE_WRITE_DENIED
    }

    private String getCurrentUserNickname(Long userId) {
        return getActiveUser(userId).getNickname();
    }
}