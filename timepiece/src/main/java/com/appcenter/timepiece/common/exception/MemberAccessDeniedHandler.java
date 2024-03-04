package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.dto.CommonResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MemberAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("[ClientAccessDeniedException] 접근 권한이 없습니다.");
        CommonResponseDto commonResponseDto = new CommonResponseDto(0, ExceptionMessage.TOKEN_UNAUTHENTICATED.getMessage(), null);

        response.setStatus(403);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(commonResponseDto));
    }
}
