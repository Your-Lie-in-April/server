package com.appcenter.timepiece.global.exception;

import com.appcenter.timepiece.domain.project.dto.ProjectCreateUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectDateValidator implements ConstraintValidator<ProjectDateConstraint, ProjectCreateUpdateRequest> {

    @Override
    public boolean isValid(ProjectCreateUpdateRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }

        return request.getEndDate().isAfter(request.getStartDate());
    }
}