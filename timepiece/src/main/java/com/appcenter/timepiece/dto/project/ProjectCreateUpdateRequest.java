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
                "color", "coverId"})
public class ProjectCreateUpdateRequest {

    @Schema(description = "제목", example = "프로젝트 제목입니다.")
    private String title;

    @Schema(description = "설명", example = "프로젝트에 대한 설명입니다.")
    private String description;

    @Schema(description = "프로젝트 시작일", example = "2024-01-18")
    private LocalDate startDate;

    @Schema(description = "프로젝트 종료일", example = "2024-02-18")
    private LocalDate endDate;

    @Schema(description = "프로젝트 시작 시간", example = "09:00:00", type = "String", pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Schema(description = "프로젝트 종료 시간", example = "22:00:00", type = "String", pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Schema(description = "요일", example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\", \"SATURDAY\"]")
    private Set<DayOfWeek> daysOfWeek;

    @Schema(description = "배경색", example = "FFFFFF")
    private String color;

    @Schema(description = "커버 이미지 식별자",
            example = "10")
    private String coverImageId;

    @Builder
    public ProjectCreateUpdateRequest(String title, String description, LocalDate startDate, LocalDate endDate,
                                      LocalTime startTime, LocalTime endTime,
                                      Set<DayOfWeek> daysOfWeek, String color, String coverImageId) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
        this.color = color;
        this.coverImageId = coverImageId;
    }

    @Override
    public String toString() {
        return "{" + "title = " + title + ", description = " + description + ", startDate = " + startDate + ", endDate = " + endDate +
                ", startTime = " + startTime + ", endTime = " + endTime + ", daysOfWeek = " + daysOfWeek +
                ", color = " + color + ", coverId = " + coverImageUrl + "}";
    }
}
