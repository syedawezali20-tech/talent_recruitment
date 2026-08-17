package com.example.talentrecruitment.candidate.dto;

import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String skills;
    private Integer experience;
    private String resumeUrl;
    private CandidateStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
