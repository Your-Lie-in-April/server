package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Schedule;
import com.appcenter.timepiece.dto.schedule.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final MemberProjectRepository memberProjectRepository;

    @Transactional
    public void createSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        List<Schedule> schedulesToSave = request.getSchedule().stream()
                .flatMap(scheduleDayRequest -> scheduleDayRequest.getSchedule().stream())
                .map(scheduleDto -> Schedule.of(scheduleDto, memberProject))
                .collect(Collectors.toList());

        scheduleRepository.saveAll(schedulesToSave);
    }

    public void editSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

    }

    public void deleteSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

    }

}
