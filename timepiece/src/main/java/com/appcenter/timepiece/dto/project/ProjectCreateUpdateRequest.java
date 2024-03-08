package com.appcenter.timepiece.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "프로젝트 생성 및 수정 요청",
        requiredProperties = {
                "title", "description", "startDate", "endDate",
                "startTime", "endTime", "mon", "tue", "wed", "thu", "fri", "sat", "sun",
                "isStored", "color", "coverId"})
public class ProjectCreateUpdateRequest {

    @Schema(description = "제목", example = "프로젝트 제목입니다.")
    private String title;

    @Schema(description = "설명", example = "프로젝트에 대한 설명입니다.")
    private String description;

    @Schema(description = "프로젝트 시작일", example = "2024-01-18")
    private LocalDate startDate;

    @Schema(description = "프로젝트 종료일", example = "2024-01-31")
    private LocalDate endDate;

    @Schema(description = "프로젝트 시작 시간", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "프로젝트 종료 시간", example = "22:00:00")
    private LocalTime endTime;

    @Schema(description = "월요일", example = "true")
    private Boolean mon;

    @Schema(description = "화요일", example = "true")
    private Boolean tue;

    @Schema(description = "수요일", example = "true")
    private Boolean wed;

    @Schema(description = "목요일", example = "true")
    private Boolean thu;

    @Schema(description = "금요일", example = "true")
    private Boolean fri;

    @Schema(description = "토요일", example = "false")
    private Boolean sat;

    @Schema(description = "일요일", example = "false")
    private Boolean sun;

    @Schema(description = "보관여부", example = "false")
    private Boolean isStored;

    @Schema(description = "배경색", example = "FFFFFF")
    private String color;

    @Schema(description = "커버가 저장된 URL",
            example = "https://cover-images.inuappcenter.com/121238128/308169809-wf61e-49f5-a5fa-2")
    private String coverImageUrl;
}
