package com.appcenter.timepiece.service;

import com.appcenter.timepiece.dto.project.ProjectResponse;
import com.appcenter.timepiece.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectResponse> findAll() {
        return projectRepository.findAllWithCover().stream().map(e -> ProjectResponse.from(e, e.getCover().getCoverImageUrl())).toList();
    }
}
