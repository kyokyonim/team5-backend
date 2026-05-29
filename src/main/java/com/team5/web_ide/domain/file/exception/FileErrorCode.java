package com.team5.web_ide.domain.file.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {

    PROJECT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PROJECT_NOT_FOUND",
            "존재하지 않는 프로젝트입니다."
    ),

    PROJECT_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "PROJECT_ACCESS_DENIED",
            "프로젝트 접근 권한이 없습니다."
    ),

    USER_STATUS_BLOCKED(
            HttpStatus.FORBIDDEN,
            "USER_STATUS_BLOCKED",
            "비활성화된 사용자는 IDE/File 기능에 접근할 수 없습니다."
    ),

    FILE_WRITE_DENIED(
            HttpStatus.FORBIDDEN,
            "FILE_WRITE_DENIED",
            "파일 생성/수정/저장 권한이 없습니다."
    ),

    FILE_DELETE_DENIED(
            HttpStatus.FORBIDDEN,
            "FILE_DELETE_DENIED",
            "삭제 권한이 없습니다."
    ),

    FILE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "FILE_NOT_FOUND",
            "존재하지 않는 파일 또는 폴더입니다."
    ),

    PARENT_FOLDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PARENT_FOLDER_NOT_FOUND",
            "상위 폴더를 찾을 수 없습니다."
    ),

    PARENT_NOT_FOLDER(
            HttpStatus.BAD_REQUEST,
            "PARENT_NOT_FOLDER",
            "상위 항목이 폴더가 아닙니다."
    ),

    NOT_FILE(
            HttpStatus.BAD_REQUEST,
            "NOT_FILE",
            "폴더는 에디터에서 열 수 없습니다."
    ),

    INVALID_FILE_NAME(
            HttpStatus.BAD_REQUEST,
            "INVALID_FILE_NAME",
            "사용할 수 없는 파일명입니다."
    ),

    DUPLICATE_FILE_NAME(
            HttpStatus.CONFLICT,
            "DUPLICATE_FILE_NAME",
            "같은 위치에 동일한 이름이 이미 존재합니다."
    ),

    FILE_VERSION_CONFLICT(
            HttpStatus.CONFLICT,
            "FILE_VERSION_CONFLICT",
            "다른 사용자가 먼저 저장했습니다. 최신 파일을 다시 불러온 후 저장해주세요."
    ),

    CONTENT_TOO_LARGE(
            HttpStatus.BAD_REQUEST,
            "CONTENT_TOO_LARGE",
            "저장 가능한 파일 크기를 초과했습니다."
    ),

    FILE_LOCKED(
            HttpStatus.LOCKED,
            "FILE_LOCKED",
            "다른 사용자가 편집 중인 파일입니다."
    ),

    FILE_LOCK_REQUIRED(
            HttpStatus.FORBIDDEN,
            "FILE_LOCK_REQUIRED",
            "파일 잠금 후 저장할 수 있습니다."
    ),

    FILE_LOCK_OWNER_MISMATCH(
            HttpStatus.FORBIDDEN,
            "FILE_LOCK_OWNER_MISMATCH",
            "파일을 잠근 사용자만 저장할 수 있습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}