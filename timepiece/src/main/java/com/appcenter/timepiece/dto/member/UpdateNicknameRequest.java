package com.appcenter.timepiece.dto.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateNicknameRequest {

    private Long projectId;

    private String nickname;

}
