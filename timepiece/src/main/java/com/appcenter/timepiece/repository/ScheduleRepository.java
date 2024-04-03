package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from Schedule s " +
               "where s.memberProject.id = :memberProjectId " +
               "and s.startTime >= :start " +
               "and s.endTime <= :end")
    void deleteMemberSchedulesBetween(Long memberProjectId, LocalDateTime start, LocalDateTime end);

    @Query("select s from Schedule s where s.memberProject.id = :memberProjectId and s.startTime between :start and :end")
    List<Schedule> findMemberWeekSchedule(Long memberProjectId, LocalDateTime start, LocalDateTime end);

    @Query("select s from Schedule s where s.memberProject.id in :memberProjectId and s.startTime between :start and :end")
    List<Schedule> findMembersWeekSchedule(List<Long> memberProjectId, LocalDateTime start, LocalDateTime end);
}
