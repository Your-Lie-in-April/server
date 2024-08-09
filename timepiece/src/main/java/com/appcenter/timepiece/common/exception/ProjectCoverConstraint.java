package com.appcenter.timepiece.common.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ProjectCoverValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectCoverConstraint {
    String message() default "프로젝트 커버로 이미지와 색상 중 하나를 선택 해 주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}