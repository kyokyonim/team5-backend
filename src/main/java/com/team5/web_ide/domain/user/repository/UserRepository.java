package com.team5.web_ide.domain.user.repository;

import com.team5.web_ide.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndProvider(String email, User.Provider provider);
    Optional<User> findByEmailAndProvider(String email, User.Provider provider);
}