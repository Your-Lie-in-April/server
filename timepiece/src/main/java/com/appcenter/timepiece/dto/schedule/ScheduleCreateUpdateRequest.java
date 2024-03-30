package com.appcenter.timepiece.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "스케줄 생성 및 수정 요청", requiredProperties = {"projectId", "schedule"})
public class ScheduleCreateUpdateRequest {

    @Schema(description = "주 단위 스케줄", example = "[" +
            "{" +
            "\"schedule\" : [{\"startTime\": \"2024-01-31T17:00:00+09:00\", \"endTime\": \"2024-01-31T20:30:00+09:00\"}," +
            "{\"startTime\": \"2024-01-31T21:30:00+09:00\", \"endTime\": \"2024-01-31T22:30:00+09:00\"}]" +
            "}," +
            "{" +
            "\"schedule\" : [{\"startTime\": \"2024-02-1T10:30:00+09:00\", \"endTime\":\"2024-02-1T11:30:00+09:00\" }]" +
            "}," +
            "{" +
            "\"schedule\" : [{\"startTime\": \"2024-02-2T10:30:00+09:00\", \"endTime\":\"2024-02-2T11:30:00+09:00\" }]" +
            "}," +
            "{\n" +
            "\"schedule\" : [{\"startTime\": \"2024-02-3T10:30:00+09:00\", \"endTime\":\"2024-02-3T11:30:00+09:00\" }]" +
            "}," +
            "{" +
            "\"schedule\" : [{\"startTime\": \"2024-02-4T10:30:00+09:00\", \"endTime\":\"2024-02-4T11:30:00+09:00\" }]" +
            "}" +
            "]")
    private List<ScheduleDayRequest> schedule;

}
