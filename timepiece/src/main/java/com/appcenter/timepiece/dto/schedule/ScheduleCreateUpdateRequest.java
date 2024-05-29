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

    @Schema(description = "주 단위 스케줄", example =
            """
              [
                {
                  "schedule": [
                    {
                      "startTime": "2024-01-31T17:00:00",
                      "endTime": "2024-01-31T20:30:00"
                    },
                    {
                      "startTime": "2024-01-31T21:30:00",
                      "endTime": "2024-01-31T22:00:00"
                    }
                  ]
                },
                {
                  "schedule": [
                    {
                      "startTime": "2024-02-01T10:30:00",
                      "endTime": "2024-02-01T11:30:00"
                    }
                  ]
                },
                {
                  "schedule": [
                    {
                      "startTime": "2024-02-02T10:30:00",
                      "endTime": "2024-02-02T11:30:00"
                    }
                  ]
                },
                {
                  "schedule": [
                    {
                      "startTime": "2024-02-03T10:30:00",
                      "endTime": "2024-02-03T11:30:00"
                    }
                  ]
                }
              ]
            """)
    private List<ScheduleDayRequest> schedule;

    public ScheduleCreateUpdateRequest(List<ScheduleDayRequest> schedule) {
        this.schedule = schedule;
    }
}
