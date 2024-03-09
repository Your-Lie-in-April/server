package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;

    @GetMapping(value = "/all")
    public ResponseEntity<CommonResponseDto> allUsers(){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, "성공", memberService.getAllMember()));

    }

    @GetMapping(value = "/state")
    public ResponseEntity<CommonResponseDto> getUserStatus(HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, "성공", memberService.getMemberState(request)));

    }

    @PutMapping(value = "/state")
    public ResponseEntity<CommonResponseDto> editUserStatus(@PathVariable String status, HttpServletRequest request){
        memberService.editMemberState(status,request);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, "성공", null));

    }


//    @PostMapping (value = "nickname")
//    public ResponseEntity<CommonResponseDto> editUserNickname(Long projectId,HttpServletRequest request){
//
//        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, "성공", oAuth2Service.getGoogleInfo(authCode)));

//    }

}
