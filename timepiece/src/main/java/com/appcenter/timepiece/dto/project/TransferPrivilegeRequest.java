package com.appcenter.timepiece.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferPrivilegeRequest {
    
    @NotNull(message = "권한을 넘겨줄 멤버의 아이디를 입력 해 주세요.")
    Long toMemberId;

    @Override
    public String toString() {
        return "toMemberId = " + toMemberId;
    }
}
