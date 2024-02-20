package com.appcenter.timepiece.presentation;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/v1/projects/members/{memberId}")
    public ResponseEntity<CommonResponse<?>> findProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(new CommonResponse<>("SUCCESS", "",
                projectService.findProjects(memberId)));
    }

    @GetMapping("/v1/projects/members/{memberId}/pin")
    public ResponseEntity<CommonResponse<?>> findPinProjects(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(new CommonResponse<>("SUCCESS", "",
                projectService.findPinProjects(memberId)));
    }

    @GetMapping("/v1/projects/members/{memberId}/{keyword}")
    public ResponseEntity<CommonResponse<?>> searchProjects(@PathVariable Long memberId,
                                                            @PathVariable String keyword) {
        return ResponseEntity.ok().body(new CommonResponse<>("SUCCESS", "",
                projectService.searchProjects(memberId, keyword)));
    }

    // todo: 해당 기능은 Project가 아닌 Member의 책임이 아닐까?
    @GetMapping("/v1/projects/{projectId}/members")
    public ResponseEntity<CommonResponse<?>> findMembersInProject(@PathVariable Long projectId) {
        return ResponseEntity.ok().body(new CommonResponse<>("SUCCESS", "",
                projectService.findMembers(projectId)));
    }

    @PostMapping("/v1/projects")
    public ResponseEntity<Void> createProject(@RequestBody ProjectCreateUpdateRequest request) {
        projectService.createProject(request);
        return ResponseEntity.accepted().build();
    }
}
