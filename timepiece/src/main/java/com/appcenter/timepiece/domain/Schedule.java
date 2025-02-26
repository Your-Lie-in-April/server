package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import com.appcenter.timepiece.dto.schedule.ScheduleDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_project_id")
    private Long memberProjectId;

    // 항상 start <= end를 만족해야함
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Builder(access = AccessLevel.PRIVATE)
    private Schedule(Long memberProjectId, LocalDateTime startTime, LocalDateTime endTime) {
        this.memberProjectId = memberProjectId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Schedule of(ScheduleDto scheduleDto, MemberProject memberProject) {
        return Schedule.builder()
                .memberProjectId(memberProject.getId())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .build();
    }
}
