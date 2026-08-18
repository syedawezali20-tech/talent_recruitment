package com.example.talentrecruitment.candidate.specification;

import com.example.talentrecruitment.candidate.entity.Candidate;
import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import org.springframework.data.jpa.domain.Specification;

public final class CandidateSpecification {

    private CandidateSpecification() {
    }

    public static Specification<Candidate> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                firstName == null || firstName.isBlank()
                        ? null
                        : criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstName")),
                                "%" + firstName.trim().toLowerCase() + "%"
                        );
    }

    public static Specification<Candidate> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) ->
                lastName == null || lastName.isBlank()
                        ? null
                        : criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                "%" + lastName.trim().toLowerCase() + "%"
                        );
    }

    public static Specification<Candidate> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                email == null || email.isBlank()
                        ? null
                        : criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + email.trim().toLowerCase() + "%"
                        );
    }

    public static Specification<Candidate> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                phone == null || phone.isBlank()
                        ? null
                        : criteriaBuilder.like(
                                root.get("phone"),
                                "%" + phone.trim() + "%"
                        );
    }

    public static Specification<Candidate> hasSkill(String skill) {
        return (root, query, criteriaBuilder) ->
                skill == null || skill.isBlank()
                        ? null
                        : criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("skills")),
                                "%" + skill.trim().toLowerCase() + "%"
                        );
    }

    public static Specification<Candidate> hasExperience(Integer experience) {
        return (root, query, criteriaBuilder) ->
                experience == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("experience"),
                                experience
                        );
    }

    public static Specification<Candidate> hasStatus(CandidateStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("status"),
                                status
                        );
    }
}