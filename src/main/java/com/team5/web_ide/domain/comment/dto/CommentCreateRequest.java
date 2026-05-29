package com.team5.web_ide.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "작성자 ID를 입력해주세요.")
    private Long writerId;

    @Size(max = 1000, message = "파일 경로는 1000자 이하여야 합니다.")
    private String filePath;

    @PositiveOrZero(message = "라인 번호는 0 이상이어야 합니다.")
    private Integer lineNumber;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(max = 5000, message = "댓글은 5000자 이하여야 합니다.")
    private String content;
}
