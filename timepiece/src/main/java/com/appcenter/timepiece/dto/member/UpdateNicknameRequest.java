package com.appcenter.timepiece.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "사용자 닉네임 변경 요청에 사용됩니다.",
        requiredProperties = {"projectId", "nickname"})
public class UpdateNicknameRequest {

    @Schema(description = "프로젝트 식별자", example = "123")
    private Long projectId;

    @Schema(description = "변경할 닉네임(To)", example = "나무")
    private String nickname;

    public UpdateNicknameRequest(Long projectId, String nickname) {
        this.projectId = projectId;
        this.nickname = nickname;
    }
}
