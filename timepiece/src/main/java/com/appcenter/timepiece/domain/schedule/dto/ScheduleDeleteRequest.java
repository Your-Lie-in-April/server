package com.appcenter.timepiece.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "스케줄 삭제 요청", requiredProperties = {"projectId", "startDate", "endDate"})
public class ScheduleDeleteRequest {

    @Schema(description = "시작일", example = "2024-01-29")
    @NotNull(message = "시작 날짜 입력은 필수입니다.")
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2024-02-02")
    @NotNull(message = "종료 날짜 입력은 필수입니다.")
    private LocalDate endDate;

    public ScheduleDeleteRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "{" + ", startDate = " + startDate + ", endDate = " + endDate + "}";
    }
}
