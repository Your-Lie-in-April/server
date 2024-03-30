package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.dto.schedule.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.dto.schedule.ScheduleDeleteRequest;
import com.appcenter.timepiece.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    //현재 ScheduleCreateUpdateRequest에서 projectId를 이미 받고있는데 @pathvariable로 또 받고있습니다. 수정이 필요해보입니다.
    @PostMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponse<?>> createSchedule(@RequestBody ScheduleCreateUpdateRequest request
            , @PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        scheduleService.createSchedule(request, projectId, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("성공", null));
    }

    @PutMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponse<?>> updateSchedule(@RequestBody ScheduleCreateUpdateRequest request
            , @PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @DeleteMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponse<?>> deleteSchedule(@RequestBody ScheduleDeleteRequest request
            , @PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }
}
