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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * For Test During Develop
     *
     * @return CommonResponse<List < ScheduleWeekResponse>>
     */
    @GetMapping("/v1/schedules/all")
    public CommonResponse<?> findAllSchedules() {
        return CommonResponse.success("성공", null);
    }

    /**
     * {@summary 인증 성공 시 프로젝트 내 모든 사용자 시간표 조회}
     * <p>프로젝트에 속해있는 모든 사용자의 시간표 정보{@literal ((List<ScheduleWeekResponse>) 반환}
     *
     * @param projectId   프로젝트 식별자
     * @param userDetails JWT 인증 시, SecurityContext에 저장된 사용자 정보
     * @return {@literal List<ScheduleWeekResponse>}
     */
    @GetMapping("/v1/projects/{projectId}/schedules")
    public CommonResponse<List<?>> findMembersSchedules(@PathVariable Long projectId,
                                                        @RequestParam LocalDate condition,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("성공", scheduleService.findMembersSchedules(projectId, condition, userDetails));
    }

    /**
     * {@summary 인증 성공 시, 특정 프로젝트-특정 사용자의 시간표 조회}
     * <p> projectId에 속하는 프로젝트 내에서 memberId와 일치하는 사용자의 일주일 스케줄을 조회한다.
     * 속해있지 않은 프로젝트에 작성된 스케줄은 조회하지 못하고, 내가 현재 속해있는 프로젝트 안에서만 사용가능하다.
     *
     * @param projectId   프로젝트 식별자
     * @param memberId    멤버 식별자
     * @param userDetails JWT 인증 시, SecurityContext에 저장된 사용자 정보
     * @return {@literal CommmonResponse<ScheduleWeekResponse>}
     */
    @GetMapping("/v1/projects/{projectId}/members/{memberId}/schedules")
    public CommonResponse<?> findSchedule(@PathVariable Long projectId,
                                          @PathVariable Long memberId,
                                          @RequestParam LocalDate condition,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("성공", scheduleService.findSchedule(projectId, memberId, condition, userDetails));
    }

    /**
     * {@summary 인증이 성공하면 request에 맞춰 스케줄을 생성한다.}
     * <p>
     *
     * @param request     스케줄 생성 및 삭제 요청 시, 사용되는 DTO(ScheduleCreateUpdateRequest)
     * @param projectId   프로젝트 식별자
     * @param userDetails JWT 인증 시, SecurityContext에 저장된 사용자 정보
     * @return
     */
    @PostMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponse<?>> createSchedule(@PathVariable Long projectId,
                                                            @RequestBody ScheduleCreateUpdateRequest request,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        scheduleService.createSchedule(request, projectId, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("성공", null));
    }

    /**
     * {@summary 인증이 성공하면 request에 맞춰 스케줄을 수정한다.}
     * <p>
     *
     * @param request     스케줄 생성 및 삭제 요청 시, 사용되는 DTO(ScheduleCreateUpdateRequest)
     * @param projectId   프로젝트 식별자
     * @param userDetails JWT 인증 시, SecurityContext에 저장된 사용자 정보
     * @return
     */
    @PutMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponse<?>> updateSchedule(@PathVariable Long projectId,
                                                            @RequestBody ScheduleCreateUpdateRequest request,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        scheduleService.editSchedule(request, projectId, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("성공", null));
    }

    /**
     * {@summary 인증이 성공하면 request에 맞춰 스케줄을 삭제한다.}
     * <p>
     *
     * @param request
     * @param projectId   프로젝트 식별자
     * @param userDetails JWT 인증 시, SecurityContext에 저장된 사용자 정보
     * @return
     */
    @DeleteMapping("/v1/projects/{projectId}/schedules")
    public ResponseEntity<CommonResponse<?>> deleteSchedule(@PathVariable Long projectId,
                                                            @RequestBody ScheduleDeleteRequest request,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        scheduleService.deleteSchedule(request, projectId, userDetails);
        return ResponseEntity.ok().body(CommonResponse.success("성공", null));
    }
}
