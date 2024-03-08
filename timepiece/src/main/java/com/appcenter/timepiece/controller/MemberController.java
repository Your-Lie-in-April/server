package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.dto.member.UpdateNicknameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    @PutMapping("/v1/projects/members/nickname")
    public ResponseEntity<CommonResponseDto<?>> updateNickname(@RequestBody UpdateNicknameRequest request) {
        return null;
    }
}
