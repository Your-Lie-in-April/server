package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ProjectDateValidator implements ConstraintValidator<ProjectDateConstraint, ProjectCreateUpdateRequest> {

    @Override
    public boolean isValid(ProjectCreateUpdateRequest projectDto, ConstraintValidatorContext context) {
        if (projectDto.getStartDate() == null || projectDto.getEndDate() == null) {
            return true;
        }

        return !projectDto.getEndDate().isBefore(projectDto.getStartDate());
    }
}