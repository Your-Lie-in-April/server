package com.appcenter.timepiece.controller;

import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    @PostMapping("/v1/projects")
    public ResponseEntity<CommonResponseDto<?>> createProject(@RequestBody ProjectCreateUpdateRequest request) {
        return null;
    }

    @PutMapping("/v1/projects/{projectId}")
    public ResponseEntity<CommonResponseDto<?>> updateProject(@RequestBody ProjectCreateUpdateRequest request) {
        return null;
    }
}
