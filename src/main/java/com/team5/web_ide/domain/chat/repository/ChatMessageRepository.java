package com.team5.web_ide.domain.chat.repository;

import com.team5.web_ide.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @EntityGraph(attributePaths = "sender")
    List<ChatMessage> findByProjectIdOrderByIdDesc(Long projectId, Pageable pageable);

    @EntityGraph(attributePaths = "sender")
    List<ChatMessage> findByProjectIdAndIdLessThanOrderByIdDesc(Long projectId, Long beforeId, Pageable pageable);
}
