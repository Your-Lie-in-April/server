package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.service.MemberService;
import com.appcenter.timepiece.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommonResponse> allUsers() {

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", memberService.getAllMember()));

    }

    @GetMapping(value = "/v1/members/{memberId}")
    public ResponseEntity<CommonResponse> MemberInfo(@PathVariable Long memberId) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", memberService.getMemberInfo(memberId)));
    }

    @PutMapping(value = "/v1/projects/members/nickname")
    public ResponseEntity<CommonResponse> editUserNickname(Long projectId, String nickname, HttpServletRequest request) {
        memberService.editMemberNickname(projectId, nickname, request);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));

    }

    @PutMapping(value = "/v1/members/{state}")
    public ResponseEntity<CommonResponse> editUserState(@PathVariable String state, HttpServletRequest request) {
        memberService.editMemberState(state, request);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));

    }

    @PostMapping("/v1/members/storage/{projectId}")
    public ResponseEntity<CommonResponse> storeProject(@PathVariable Long projectId, HttpServletRequest request) {
        memberService.storeProject(projectId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));
    }

    @DeleteMapping("/v1/members/storage/{projectId}")
    public ResponseEntity<CommonResponse> deleteStoreProject(@PathVariable Long projectId, HttpServletRequest request) {
        memberService.deleteStoredProject(projectId, request);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));
    }

    @PatchMapping("/v1/members/pin/{projectId}")
    public CommonResponse<?> pinProject(@PathVariable Long projectId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        projectService.pinProject(projectId, userDetails);
        return CommonResponse.success("프로젝트 핀 설정에 성공했습니다.", null);
    }

}