package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectTimeValidator implements ConstraintValidator<ProjectTimeConstraint, ProjectCreateUpdateRequest> {

    @Override
    public boolean isValid(ProjectCreateUpdateRequest request, ConstraintValidatorContext context) {

        if (request.getStartTime() == null || request.getEndTime() == null) {
            return true;
        }

        if (request.getEndTime().toString().equals("00:00") && !request.getStartTime().toString().equals("00:00")) {
            return true;
        }

        return request.getEndTime().isAfter(request.getStartTime());
    }
}