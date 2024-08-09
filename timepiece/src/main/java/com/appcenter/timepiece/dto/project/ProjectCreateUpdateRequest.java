package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.common.exception.ProjectCoverConstraint;
import com.appcenter.timepiece.common.exception.ProjectDateConstraint;
import com.appcenter.timepiece.common.exception.ProjectTimeConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
                "color", "coverImageId"})
@ProjectDateConstraint(message = "프로젝트 종료일은 시작일을 앞설 수 없습니다.")
@ProjectTimeConstraint(message = "프로젝트 종료시간은 시작시간을 앞설 수 없습니다.")
@ProjectCoverConstraint(message = "프로젝트 커버로 이미지와 색상 중 하나를 선택 해 주세요")
public class ProjectCreateUpdateRequest {

    @Schema(description = "제목", example = "프로젝트 제목입니다.")
    @NotBlank(message = "제목 입력은 필수입니다.")
    private String title;

    @Schema(description = "설명", example = "프로젝트에 대한 설명입니다.")
    private String description;

    @Schema(description = "프로젝트 시작일", example = "2024-01-18")
    @NotNull(message = "프로젝트 시작일은 필수입니다.")
    private LocalDate startDate;

    @Schema(description = "프로젝트 종료일", example = "2024-02-18")
    @NotNull(message = "프로젝트 종료일은 필수입니다.")
    private LocalDate endDate;

    @Schema(description = "프로젝트 시작 시간", example = "09:00:00", type = "String", pattern = "HH:mm:ss")
    @NotNull(message = "프로젝트 시작시간 입력은 필수입니다.")
    private LocalTime startTime;

    @Schema(description = "프로젝트 종료 시간", example = "22:00:00", type = "String", pattern = "HH:mm:ss")
    @NotNull(message = "프로젝트 종료시간 입력은 필수입니다.")
    private LocalTime endTime;

    @Schema(description = "요일", example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\", \"SATURDAY\"]")
    @NotEmpty(message = "요일 선택은 필수입니다.")
    private Set<DayOfWeek> daysOfWeek;

    @Schema(description = "배경색", example = "#FFFFFF")
    @Pattern(regexp = "^$|^#([A-Fa-f0-9]{6})$", message = "유효하지 않은 색상 코드입니다.")
    private String color = "#000000";

    @Schema(description = "커버 이미지 식별자", example = "10")
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
                ", color = " + color + ", coverId = " + coverImageId + "}";
    }
}
