package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import com.appcenter.timepiece.dto.schedule.ScheduleDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_project_id")
    private MemberProject memberProject;

    // 항상 start <= end를 만족해야함
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Builder(access = AccessLevel.PRIVATE)
    private Schedule(MemberProject memberProject, LocalDateTime startTime, LocalDateTime endTime) {
        this.memberProject = memberProject;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Schedule of(ScheduleDto scheduleDto, MemberProject memberProject) {
        return Schedule.builder()
                .memberProject(memberProject)
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .build();
    }
}
