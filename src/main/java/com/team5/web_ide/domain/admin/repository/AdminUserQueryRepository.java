package com.team5.web_ide.domain.admin.repository;

import com.team5.web_ide.domain.admin.dto.AdminUserCategory;
import com.team5.web_ide.domain.admin.dto.AdminUserStatusFilter;
import com.team5.web_ide.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AdminUserQueryRepository {

    private final EntityManager entityManager;

    public Page<User> findUsers(
            AdminUserCategory category,
            AdminUserStatusFilter status,
            String keyword,
            LocalDateTime newUserSince,
            Pageable pageable
    ) {
        QueryParts queryParts = buildQueryParts(category, status, keyword);

        TypedQuery<User> contentQuery = entityManager.createQuery("""
                select u
                from User u
                where %s
                order by u.createdAt desc, u.id desc
                """.formatted(queryParts.whereClause()), User.class);
        applyParameters(contentQuery, queryParts.parameters(), newUserSince);

        TypedQuery<Long> countQuery = entityManager.createQuery("""
                select count(u)
                from User u
                where %s
                """.formatted(queryParts.whereClause()), Long.class);
        applyParameters(countQuery, queryParts.parameters(), newUserSince);

        List<User> users = contentQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        Long totalCount = countQuery.getSingleResult();

        return new PageImpl<>(users, pageable, totalCount);
    }

    private QueryParts buildQueryParts(
            AdminUserCategory category,
            AdminUserStatusFilter status,
            String keyword
    ) {
        StringBuilder where = new StringBuilder("1 = 1");
        Map<String, Object> parameters = new HashMap<>();

        if (category == AdminUserCategory.NEW) {
            where.append(" and u.createdAt >= :newUserSince");
        }

        if (status == AdminUserStatusFilter.ACTIVE) {
            where.append(" and u.status = :status");
            parameters.put("status", User.Status.ACTIVE);
        }
        if (status == AdminUserStatusFilter.BANNED) {
            where.append(" and u.status = :status");
            parameters.put("status", User.Status.BANNED);
        }

        String normalizedKeyword = normalizeKeyword(keyword);
        if (normalizedKeyword != null) {
            where.append(" and (lower(u.nickname) like :keyword or lower(u.email) like :keyword)");
            parameters.put("keyword", "%" + normalizedKeyword + "%");
        }

        return new QueryParts(where.toString(), parameters);
    }

    private void applyParameters(
            TypedQuery<?> query,
            Map<String, Object> parameters,
            LocalDateTime newUserSince
    ) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        if (query.getParameters().stream().anyMatch(parameter -> "newUserSince".equals(parameter.getName()))) {
            query.setParameter("newUserSince", newUserSince);
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim().toLowerCase();
    }

    private record QueryParts(String whereClause, Map<String, Object> parameters) {
    }
}
