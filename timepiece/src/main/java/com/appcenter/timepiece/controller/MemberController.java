package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final ProjectService projectService;

    @PatchMapping("/v1/members/pin/{projectId}")
    public CommonResponse<?> pinProject(@PathVariable Long projectId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        projectService.pinProject(projectId, userDetails);
        return CommonResponse.success("프로젝트 핀 설정에 성공했습니다.", null);
    }
}
