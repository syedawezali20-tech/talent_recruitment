package com.example.talentrecruitment.job.repository;

import com.example.talentrecruitment.job.entity.Job;
import com.example.talentrecruitment.job.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByDepartmentContainingIgnoreCase(String department, Pageable pageable);

    Page<Job> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Page<Job> findByDepartmentContainingIgnoreCaseAndStatus(String department, JobStatus status, Pageable pageable);

    Page<Job> findByLocationContainingIgnoreCaseAndStatus(String location, JobStatus status, Pageable pageable);
}
