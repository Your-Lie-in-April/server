package com.appcenter.timepiece.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "프로젝트 생성 및 수정 요청",
        requiredProperties = {
                "title", "description", "startDate", "endDate",
                "startTime", "endTime", "daysOfWeek",
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

    @Schema(description = "프로젝트 시작 시간", example = "09:00:00", type = "String", pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Schema(description = "프로젝트 종료 시간", example = "22:00:00", type = "String", pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Schema(description = "요일", example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\"]")
    private Set<DayOfWeek> daysOfWeek;

    @Schema(description = "보관여부", example = "false")
    private Boolean isStored;

    @Schema(description = "배경색", example = "FFFFFF")
    private String color;

    @Schema(description = "커버가 저장된 URL",
            example = "https://cover-images.inuappcenter.com/121238128/308169809-wf61e-49f5-a5fa-2")
    private String coverImageUrl;

    @Builder
    private ProjectCreateUpdateRequest(String title, String description, LocalDate startDate, LocalDate endDate,
                                      LocalTime startTime, LocalTime endTime, Set<DayOfWeek> daysOfWeek,
                                      Boolean isStored, String color, String coverImageUrl) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
        this.isStored = isStored;
        this.color = color;
        this.coverImageUrl = coverImageUrl;
    }
}
