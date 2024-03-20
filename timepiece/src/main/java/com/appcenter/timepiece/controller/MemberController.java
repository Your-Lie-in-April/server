package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.appcenter.timepiece.service.ProjectService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;

    private final ProjectService projectService;

    @PatchMapping("/v1/members/pin/{projectId}")
    public CommonResponse<?> pinProject(@PathVariable Long projectId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        projectService.pinProject(projectId, userDetails);
        return CommonResponse.success("프로젝트 핀 설정에 성공했습니다.", null);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<CommonResponse> allUsers(){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", memberService.getAllMember()));

    }

    @GetMapping(value = "/state")
    public ResponseEntity<CommonResponse> getUserStatus(HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", memberService.getMemberState(request)));

    }

    @PutMapping(value = "/state/{state}")
    public ResponseEntity<CommonResponse> editUserState(@PathVariable String state, HttpServletRequest request){
        memberService.editMemberState(state,request);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));

    }


    @PutMapping (value = "nickname")
    public ResponseEntity<CommonResponse> editUserNickname(Long projectId,String nickname ,HttpServletRequest request){
        memberService.editMemberNickname(projectId,nickname, request);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));

    }
}