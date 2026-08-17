package com.example.talentrecruitment.job.dto;

import com.example.talentrecruitment.job.entity.EmploymentType;
import com.example.talentrecruitment.job.entity.JobStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 0, message = "Experience required cannot be negative")
    private Integer experienceRequired;

    private EmploymentType employmentType;

    private JobStatus status;
}
