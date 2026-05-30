package com.team5.web_ide.domain.file.controller;

import com.team5.web_ide.domain.file.dto.FileContentUpdateRequest;
import com.team5.web_ide.domain.file.dto.FileContentUpdateResponse;
import com.team5.web_ide.domain.file.dto.FileCreateRequest;
import com.team5.web_ide.domain.file.dto.FileCreateResponse;
import com.team5.web_ide.domain.file.dto.FileDetailResponse;
import com.team5.web_ide.domain.file.dto.FileLockResponse;
import com.team5.web_ide.domain.file.dto.FileNameValidateRequest;
import com.team5.web_ide.domain.file.dto.FileNameValidateResponse;
import com.team5.web_ide.domain.file.dto.FileRenameRequest;
import com.team5.web_ide.domain.file.dto.FileRenameResponse;
import com.team5.web_ide.domain.file.dto.FileTreeResponse;
import com.team5.web_ide.domain.file.dto.FolderCreateRequest;
import com.team5.web_ide.domain.file.service.FileService;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}")
public class FileController {

    private final FileService fileService;

    @GetMapping("/files/tree")
    public ApiResponse<List<FileTreeResponse>> getFileTree(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "파일 트리 조회에 성공했습니다.",
                fileService.getFileTree(projectId, userId)
        );
    }

    @PostMapping("/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FileCreateResponse> createFile(
            @PathVariable Long projectId,
            @Valid @RequestBody FileCreateRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "파일이 생성되었습니다.",
                fileService.createFile(projectId, userId, request)
        );
    }

    @PostMapping("/folders")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FileCreateResponse> createFolder(
            @PathVariable Long projectId,
            @Valid @RequestBody FolderCreateRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "폴더가 생성되었습니다.",
                fileService.createFolder(projectId, userId, request)
        );
    }

    @GetMapping("/files/{fileId}")
    public ApiResponse<FileDetailResponse> getFileDetail(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "파일 내용 조회에 성공했습니다.",
                fileService.getFileDetail(projectId, fileId, userId)
        );
    }

    @PatchMapping("/files/{fileId}/content")
    public ApiResponse<FileContentUpdateResponse> updateFileContent(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            @Valid @RequestBody FileContentUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "파일이 저장되었습니다.",
                fileService.updateFileContent(projectId, fileId, userId, request)
        );
    }

    @PatchMapping("/files/{fileId}/name")
    public ApiResponse<FileRenameResponse> renameFile(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            @Valid @RequestBody FileRenameRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "이름이 변경되었습니다.",
                fileService.renameFile(projectId, fileId, userId, request)
        );
    }

    @DeleteMapping("/files/{fileId}")
    public ApiResponse<Void> deleteFile(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        fileService.deleteFile(projectId, fileId, userId);

        return ApiResponse.success("파일 또는 폴더가 삭제되었습니다.");
    }

    @PostMapping("/files/validate-name")
    public ApiResponse<FileNameValidateResponse> validateName(
            @PathVariable Long projectId,
            @Valid @RequestBody FileNameValidateRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        FileNameValidateResponse response = fileService.validateName(
                projectId,
                userId,
                request
        );

        String message = Boolean.TRUE.equals(response.getValid())
                ? "사용 가능한 이름입니다."
                : "사용할 수 없는 이름입니다.";

        return ApiResponse.success(message, response);
    }

    @PostMapping("/files/{fileId}/lock")
    public ApiResponse<FileLockResponse> lockFile(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ApiResponse.success(
                "파일 잠금에 성공했습니다.",
                fileService.lockFile(projectId, fileId, userId)
        );
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Long)) {
            throw new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED);
        }

        return (Long) principal;
    }
}
