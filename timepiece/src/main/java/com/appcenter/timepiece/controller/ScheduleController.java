package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.dto.schedule.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.dto.schedule.ScheduleDeleteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScheduleController {

    @PostMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponseDto<?>> createSchedule(@RequestBody ScheduleCreateUpdateRequest request) {
        return null;
    }

    @PutMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponseDto<?>> updateSchedule(@RequestBody ScheduleCreateUpdateRequest request) {
        return null;
    }

    @DeleteMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponseDto<?>> deleteSchedule(@RequestBody ScheduleDeleteRequest request) {
        return null;
    }
}
