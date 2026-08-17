package com.example.talentrecruitment.job.service;

import com.example.talentrecruitment.job.dto.JobRequest;
import com.example.talentrecruitment.job.dto.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {

    JobResponse createJob(JobRequest request);

    JobResponse getJobById(Long id);

    Page<JobResponse> getAllJobs(String department, String location, String status, Pageable pageable);

    JobResponse updateJob(Long id, JobRequest request);

    void deleteJob(Long id);
}
