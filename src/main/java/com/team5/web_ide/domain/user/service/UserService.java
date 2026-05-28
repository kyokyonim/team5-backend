package com.team5.web_ide.domain.user.service;

import com.team5.web_ide.domain.user.dto.UpdateProfileRequestDto;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));

        if (dto.getNickname() != null) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
            }
            user.updateNickname(dto.getNickname());
        }

        if (dto.getProfileColor() != null) {
            user.updateProfileColor(dto.getProfileColor());
        }

        return userRepository.save(user);
    }
}