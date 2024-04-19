package com.appcenter.timepiece.dto.project;

import lombok.Getter;

@Getter
public class TransferPrivilegeRequest {
    Long toMemberId;

    public TransferPrivilegeRequest(Long toMemberId) {
        this.toMemberId = toMemberId;
    }
}
