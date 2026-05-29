package com.team5.web_ide.domain.presence.dto;

import com.team5.web_ide.domain.presence.entity.Presence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresenceUpdateRequest {

    @NotNull(message = "유저 ID를 입력해주세요.")
    private Long userId;

    private Presence.PresenceStatus status = Presence.PresenceStatus.ONLINE;

    @Size(max = 1000, message = "파일 경로는 1000자 이하여야 합니다.")
    private String currentFilePath;

    @PositiveOrZero(message = "커서 라인은 0 이상이어야 합니다.")
    private Integer cursorLine;

    @PositiveOrZero(message = "커서 컬럼은 0 이상이어야 합니다.")
    private Integer cursorColumn;
}
