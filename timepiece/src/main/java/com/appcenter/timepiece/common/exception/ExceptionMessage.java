package com.appcenter.timepiece.common.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionMessage {
    TOKEN_EXPIRED("토큰이 만료되었습니다.", 0, HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_AUTHORIZED("권한이 없습니다", 0, HttpStatus.FORBIDDEN),
    TOKEN_UNAUTHENTICATED("인증되지 않은 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_FORMAT("잘못된 형식의 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND("멤버가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
    TOKEN_NOT_FOUND("토큰이 비었거나 null입니다", 0, HttpStatus.BAD_REQUEST),
    TOKEN_TYPE_INVALID("토큰 타입이 틀렸습니다.", 0, HttpStatus.BAD_REQUEST),
    MEMBER_PROJECT_NOT_FOUND("멤버-프로젝트가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
    MEMBER_UNAUTHENTICATED("접근 권한이 없는 페이지입니다.", 0, HttpStatus.UNAUTHORIZED),
    PROJECT_NOT_FOUND("프로젝트가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
    IS_NOT_SAME_DAY("startTime과 endTime의 날짜가 다름", 0, HttpStatus.BAD_REQUEST),
    INVALID_PROJECT_DAY_OF_WEEK("프로젝트 수행일이 아님", 0, HttpStatus.BAD_REQUEST),
    INVALID_TIME_SEQUENCE("startTime이 endTime 이후일 수 없습니다.", 0, HttpStatus.BAD_REQUEST),
    INVALID_PROJECT_TIME("스케줄 작성 요청이 프로젝트 수행 시간을 벗어납니다.", 0, HttpStatus.BAD_REQUEST),
    INVALID_PROJECT_PERIOD("스케줄 작성 요청이 프로젝트 수행 기간을 벗어납니다.", 0, HttpStatus.BAD_REQUEST),
    INVALID_TIME_UNIT("스케줄 요청은 30분 단위의 시간이어야 합니다.", 0, HttpStatus.BAD_REQUEST),
    INVALID_DATE("ScheduleDayRequest에 속한 모든 요청은 같은 날짜여야 합니다.", 0, HttpStatus.BAD_REQUEST),
    DUPLICATE_DATE("스케줄 생성 요청에 중복된 날짜가 존재합니다.", 0, HttpStatus.BAD_REQUEST),
    INVALID_WEEK("스케줄 생성 요청이 일주일 범위를 초과합니다.", 0, HttpStatus.BAD_REQUEST),
    KICK_ADMIN("프로젝트 관리자는 강퇴할 수 없습니다", 0, HttpStatus.BAD_REQUEST),
    LINK_EXPIRED("초대링크의 유효날짜가 지났습니다.", 0, HttpStatus.BAD_REQUEST),
    DUPLICATE_SIGN_REQUEST("중복된 가입요청입니다.", 0, HttpStatus.BAD_REQUEST),
    INSUFFICIENT_PRIVILEGE("프로젝트 관리자 권한이 없습니다.", 0, HttpStatus.BAD_REQUEST),
    ADMIN_LEAVE("관리자는 나갈 수 없습니다", 0, HttpStatus.BAD_REQUEST),
    NOT_MEMBER("속하지 않은 프로젝트 정보를 조회할 수 없습니다.", 0, HttpStatus.BAD_REQUEST);


    private final String message;

    private final Integer errorCode;

    private final HttpStatus httpStatus;


    ExceptionMessage(String message, Integer errorCode, HttpStatus httpStatus) {
        this.message = message;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

