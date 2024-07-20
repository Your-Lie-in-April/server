package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectTimeValidator implements ConstraintValidator<ProjectTimeConstraint, ProjectCreateUpdateRequest> {

    @Override
    public boolean isValid(ProjectCreateUpdateRequest projectDto, ConstraintValidatorContext context) {
        if (projectDto.getStartTime() == null || projectDto.getEndTime() == null) {
            return true;
        }

        return !projectDto.getEndTime().isBefore(projectDto.getStartTime());
    }
}