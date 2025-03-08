package com.appcenter.timepiece.global.exception;

import com.appcenter.timepiece.domain.project.dto.ProjectCreateUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectCoverValidator implements ConstraintValidator<ProjectCoverConstraint, ProjectCreateUpdateRequest> {

    @Override
    public boolean isValid(ProjectCreateUpdateRequest request, ConstraintValidatorContext context) {
        if ((request.getCoverImageId().equals("") && !request.getColor().equals(""))
                || request.getColor().equals("") && !request.getCoverImageId().equals("")) {
            return true;
        } else {
            return false;
        }
    }
}