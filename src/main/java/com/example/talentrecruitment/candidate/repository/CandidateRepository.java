package com.example.talentrecruitment.candidate.repository;

import com.example.talentrecruitment.candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository
        extends JpaRepository<Candidate, Long>,
                JpaSpecificationExecutor<Candidate> {

    boolean existsByEmail(String email);
}