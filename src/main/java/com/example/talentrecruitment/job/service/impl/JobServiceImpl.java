package com.example.talentrecruitment.job.service.impl;

import com.example.talentrecruitment.common.exception.BadRequestException;
import com.example.talentrecruitment.common.exception.ResourceNotFoundException;
import com.example.talentrecruitment.job.dto.JobRequest;
import com.example.talentrecruitment.job.dto.JobResponse;
import com.example.talentrecruitment.job.entity.Job;
import com.example.talentrecruitment.job.entity.JobStatus;
import com.example.talentrecruitment.job.repository.JobRepository;
import com.example.talentrecruitment.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    public JobResponse createJob(JobRequest request) {
        Job job = Job.builder()
                .title(request.getTitle().trim())
                .department(request.getDepartment().trim())
                .description(request.getDescription().trim())
                .location(request.getLocation().trim())
                .experienceRequired(request.getExperienceRequired())
                .employmentType(request.getEmploymentType())
                .status(request.getStatus() != null ? request.getStatus() : JobStatus.OPEN)
                .build();

        if (request.getEmploymentType() == null) {
            throw new BadRequestException("Employment type is required");
        }

        Job savedJob = jobRepository.save(job);
        log.info("Created job with id {}", savedJob.getId());
        return mapToResponse(savedJob);
    }

    @Override
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return mapToResponse(job);
    }

    @Override
    public Page<JobResponse> getAllJobs(String department, String location, String status, Pageable pageable) {
        Page<Job> jobs;

        if (department != null && !department.isBlank() && status != null && !status.isBlank()) {
            JobStatus jobStatus = JobStatus.valueOf(status.trim().toUpperCase());
            jobs = jobRepository.findByDepartmentContainingIgnoreCaseAndStatus(department, jobStatus, pageable);
        } else if (location != null && !location.isBlank() && status != null && !status.isBlank()) {
            JobStatus jobStatus = JobStatus.valueOf(status.trim().toUpperCase());
            jobs = jobRepository.findByLocationContainingIgnoreCaseAndStatus(location, jobStatus, pageable);
        } else if (department != null && !department.isBlank()) {
            jobs = jobRepository.findByDepartmentContainingIgnoreCase(department, pageable);
        } else if (location != null && !location.isBlank()) {
            jobs = jobRepository.findByLocationContainingIgnoreCase(location, pageable);
        } else if (status != null && !status.isBlank()) {
            JobStatus jobStatus = JobStatus.valueOf(status.trim().toUpperCase());
            jobs = jobRepository.findByStatus(jobStatus, pageable);
        } else {
            jobs = jobRepository.findAll(pageable);
        }

        return jobs.map(this::mapToResponse);
    }

    @Override
    public JobResponse updateJob(Long id, JobRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        job.setTitle(request.getTitle().trim());
        job.setDepartment(request.getDepartment().trim());
        job.setDescription(request.getDescription().trim());
        job.setLocation(request.getLocation().trim());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setEmploymentType(request.getEmploymentType());
        job.setStatus(request.getStatus() != null ? request.getStatus() : job.getStatus());

        Job updatedJob = jobRepository.save(job);
        log.info("Updated job with id {}", updatedJob.getId());
        return mapToResponse(updatedJob);
    }

    @Override
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        jobRepository.delete(job);
        log.info("Deleted job with id {}", id);
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .department(job.getDepartment())
                .description(job.getDescription())
                .location(job.getLocation())
                .experienceRequired(job.getExperienceRequired())
                .employmentType(job.getEmploymentType())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
