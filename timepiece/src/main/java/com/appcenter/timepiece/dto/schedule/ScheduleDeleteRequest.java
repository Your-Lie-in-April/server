package com.appcenter.timepiece.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "스케줄 삭제 요청", requiredProperties = {"projectId", "startDate", "endDate"})
public class ScheduleDeleteRequest {

    @Schema(description = "프로젝트 식별자", example = "1")
    private Long projectId;

    @Schema(description = "시작일", example = "2024-01-29")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2024-02-02")
    private LocalDate endDate;

    public ScheduleDeleteRequest(Long projectId, LocalDate startDate, LocalDate endDate) {
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "{" + "projectId = " + projectId + ", startDate = " + startDate + ", endDate = " + endDate + "}";
    }
}
