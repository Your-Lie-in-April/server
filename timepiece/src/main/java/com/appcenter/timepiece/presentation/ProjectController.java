package com.appcenter.timepiece.presentation;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Just for Test!<br>
     * DB에 저장된 모든 프로젝트를 조회하여 ProjectResponse 타입의 List를 리턴합니다.
     */
    @GetMapping("/v1/projects/all")
    public ResponseEntity<CommonResponse<?>> findAllForTest() {
        return ResponseEntity.ok().body(new CommonResponse<>("SUCCESS", "", projectService.findAll()));
    }
}
