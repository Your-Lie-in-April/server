package com.appcenter.timepiece.domain.schedule.repository;

import com.appcenter.timepiece.domain.schedule.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Transactional
    @Modifying  // clearAutomatically = true 사용 시, LazyInitializationException 조심!
    @Query("delete from Schedule s " +
            "where s.memberProjectId = :memberProjectId " +
            "and s.startTime >= :start " +
            "and s.endTime <= :end")
    void deleteMemberSchedulesBetween(Long memberProjectId, LocalDateTime start, LocalDateTime end);

    @Query("select s from Schedule s where s.memberProjectId = :memberProjectId and s.startTime between :start and :end")
    List<Schedule> findMemberWeekSchedule(Long memberProjectId, LocalDateTime start, LocalDateTime end);

    @Query("select s from Schedule s where s.memberProjectId in :memberProjectId and s.startTime between :start and :end")
    List<Schedule> findMembersWeekSchedule(List<Long> memberProjectId, LocalDateTime start, LocalDateTime end);
}
