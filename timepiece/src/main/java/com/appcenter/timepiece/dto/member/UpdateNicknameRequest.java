package com.appcenter.timepiece.dto.member;

import lombok.Getter;

@Getter
public class UpdateNicknameRequest {

    private Long projectId;
    private String nickname;

}
