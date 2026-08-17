package com.example.talentrecruitment.job.service;

import com.example.talentrecruitment.common.exception.ResourceNotFoundException;
import com.example.talentrecruitment.job.dto.JobRequest;
import com.example.talentrecruitment.job.dto.JobResponse;
import com.example.talentrecruitment.job.entity.EmploymentType;
import com.example.talentrecruitment.job.entity.Job;
import com.example.talentrecruitment.job.entity.JobStatus;
import com.example.talentrecruitment.job.repository.JobRepository;
import com.example.talentrecruitment.job.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private Job job;

    @BeforeEach
    void setUp() {
        job = Job.builder()
                .id(1L)
                .title("Java Developer")
                .department("Engineering")
                .description("Develop backend services")
                .location("Hyderabad")
                .experienceRequired(3)
                .employmentType(EmploymentType.FULL_TIME)
                .status(JobStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createJob_shouldReturnJobResponse() {
        JobRequest request = JobRequest.builder()
                .title("Java Developer")
                .department("Engineering")
                .description("Develop backend services")
                .location("Hyderabad")
                .experienceRequired(3)
                .employmentType(EmploymentType.FULL_TIME)
                .status(JobStatus.OPEN)
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        JobResponse response = jobService.createJob(request);

        assertNotNull(response);
        assertEquals("Java Developer", response.getTitle());
        assertEquals("Engineering", response.getDepartment());
    }

    @Test
    void getJob_shouldReturnJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        JobResponse result = jobService.getJobById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Developer", result.getTitle());
    }

    @Test
    void getJob_whenNotFound_shouldThrow() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.getJobById(99L));
    }
}
