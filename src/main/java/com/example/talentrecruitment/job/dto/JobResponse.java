package com.example.talentrecruitment.job.dto;

import com.example.talentrecruitment.job.entity.EmploymentType;
import com.example.talentrecruitment.job.entity.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String department;
    private String description;
    private String location;
    private Integer experienceRequired;
    private EmploymentType employmentType;
    private JobStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
