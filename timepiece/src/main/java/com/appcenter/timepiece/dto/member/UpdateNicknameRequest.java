package com.appcenter.timepiece.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "사용자 닉네임 변경 요청에 사용됩니다.",
        requiredProperties = {"projectId", "nickname"})
public class UpdateNicknameRequest {
    @Schema(description = "프로젝트 식별자", example = "123")
    @NotNull(message = "프로젝트 선택은 필수입니다.")
    private Long projectId;

    @Schema(description = "변경할 닉네임(To)", example = "나무")
    @NotBlank(message = "변경 할 닉네임을 입력 해 주세요.")
    private String nickname;

    public UpdateNicknameRequest(Long projectId, String nickname) {
        this.projectId = projectId;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "{" + "projectId = " + projectId + ", nickname = " + nickname + "}";

    }
}
