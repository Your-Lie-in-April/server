package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.config.SwaggerApiResponses;
import com.appcenter.timepiece.service.MemberService;
import com.appcenter.timepiece.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class MemberController {

    private final MemberService memberService;

    private final ProjectService projectService;


    @GetMapping(value = "/v1/members/all")
    @Operation(summary = "멤버 전체 조회", description = "현재 서비스에 가입되어있는 멤버를 모두 조회한다.")
    @SwaggerApiResponses
    public CommonResponse<?> allUsers() {

        return CommonResponse.success("성공", memberService.getAllMember());
    }

    @GetMapping(value = "/v1/members/{memberId}")
    @Operation(summary = "멤버 정보 조회", description = "")
    @SwaggerApiResponses
    public CommonResponse<?> MemberInfo(@PathVariable Long memberId) {
        return CommonResponse.success("성공", memberService.getMemberInfo(memberId));
    }

    @PutMapping(value = "/v1/projects/members/nickname")
    @Operation(summary = "닉네임 재설정", description = "", deprecated = true)
    @SwaggerApiResponses
    public CommonResponse<?> editUserNickname(Long projectId, String nickname, @AuthenticationPrincipal UserDetails userDetails) {
        memberService.editMemberNickname(projectId, nickname, userDetails);
        return CommonResponse.success("성공", null);
    }

    @PutMapping(value = "/v2/projects/members/nickname")
    @Operation(summary = "닉네임 재설정 v2", description = "")
    @SwaggerApiResponses
    public CommonResponse<?> editUserNickname2(Long projectId, String nickname, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("사용자 닉네임 수정 성공했습니다.", memberService.editMemberNickname(projectId, nickname, userDetails));
    }

    @PutMapping(value = "/v1/members/{state}")
    @Operation(summary = "상태메시지 설정", description = "")
    @SwaggerApiResponses
    public CommonResponse<?> editUserState(@PathVariable String state, @AuthenticationPrincipal UserDetails userDetails) {
        memberService.editMemberState(state, userDetails);
        return CommonResponse.success("성공", null);

    }

    @PatchMapping("/v1/members/storage/{projectId}")
    @Operation(summary = "프로젝트 보관 설정/해제", description = "")
    @SwaggerApiResponses
    @Deprecated
    public CommonResponse<?> storeProject(@PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        memberService.storeProject(projectId, userDetails);
        return CommonResponse.success("성공", null);
    }

    @PatchMapping("/v2/members/storage/{projectId}")
    @Operation(summary = "프로젝트 보관설정/해제 v2(프로젝트 보관 여부 반환)")
    @SwaggerApiResponses
    public CommonResponse<?> storeProject2(@PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("성공", memberService.storeProject2(projectId, userDetails));
    }

    @PatchMapping("/v1/members/pin/{projectId}")
    @Operation(summary = "프로젝트 핀 설정/해제", description = "")
    @SwaggerApiResponses
    public CommonResponse<?> pinProject(@PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        projectService.pinProject(projectId, userDetails);
        return CommonResponse.success("프로젝트 핀 설정에 성공했습니다.", null);
    }

}