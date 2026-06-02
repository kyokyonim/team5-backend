package com.team5.web_ide.domain.admin.repository;

import com.team5.web_ide.domain.admin.dto.AdminRecentChatResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminChatQueryRepository {

    private final EntityManager entityManager;

    public List<AdminRecentChatResponse> findRecentChats(Pageable pageable) {
        TypedQuery<AdminRecentChatResponse> query = entityManager.createQuery("""
                select new com.team5.web_ide.domain.admin.dto.AdminRecentChatResponse(
                    cm.id,
                    p.id,
                    p.projectName,
                    u.id,
                    u.nickname,
                    u.profileColor,
                    cm.createdAt
                )
                from ChatMessage cm
                join cm.sender u
                join Project p on p.id = cm.projectId
                order by cm.createdAt desc, cm.id desc
                """, AdminRecentChatResponse.class);

        return query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}
