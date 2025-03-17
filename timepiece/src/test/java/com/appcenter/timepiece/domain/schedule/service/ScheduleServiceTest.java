package com.appcenter.timepiece.domain.schedule.service;

import com.appcenter.timepiece.domain.project.repository.MemberProjectRepository;
import com.appcenter.timepiece.domain.project.repository.ProjectRepository;
import com.appcenter.timepiece.domain.schedule.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ScheduleService 테스트")
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleValidator validator = new ScheduleValidator();

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MemberProjectRepository memberProjectRepository;

    @Mock
    private ProjectRepository projectRepository;


}