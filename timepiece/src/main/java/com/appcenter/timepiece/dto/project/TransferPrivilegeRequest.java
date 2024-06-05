package com.appcenter.timepiece.dto.project;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferPrivilegeRequest {
    Long toMemberId;

    @Override
    public String toString() {
        return "toMemberId = " + toMemberId;
    }
}
