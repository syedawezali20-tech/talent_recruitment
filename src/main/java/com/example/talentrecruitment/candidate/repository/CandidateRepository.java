package com.example.talentrecruitment.candidate.repository;

import com.example.talentrecruitment.candidate.entity.Candidate;
import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Page<Candidate> findBySkillsContainingIgnoreCase(String skill, Pageable pageable);

    Page<Candidate> findByStatus(CandidateStatus status, Pageable pageable);

    Page<Candidate> findBySkillsContainingIgnoreCaseAndStatus(String skill, CandidateStatus status, Pageable pageable);

    boolean existsByEmail(String email);
}
