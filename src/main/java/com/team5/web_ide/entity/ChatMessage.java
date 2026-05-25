package com.team5.web_ide.entity;
import com.team5.web_ide.domain.user.entity.User;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;
    // 채팅 내역에 몇 시에 보냈는지 확인하기 위해서
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name="sender_id",nullable = false)
    private User sender;


}
