package com.team5.web_ide.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequestDto {

    private String nickname;
    private String profileColor;
}