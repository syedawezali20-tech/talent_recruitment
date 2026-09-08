package com.example.talentrecruitment.auth.repository;

import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserSession;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByTokenHash(String tokenHash);

    List<UserSession> findByUserAndRevokedFalse(User user);

    void deleteByUser(User user);
}
