package com.appcenter.timepiece.domain.project.entity;

import com.appcenter.timepiece.domain.project.dto.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.global.common.entity.BaseTimeEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_days_of_week",
            joinColumns = @JoinColumn(name = "project_id"))
    private Set<DayOfWeek> daysOfWeek;

    @Column(name = "cover_id")
    private Long coverId;

    private String color;

    private Boolean isDeleted;

    @Builder
    private Project(String title, String description,
                    LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
                    Set<DayOfWeek> daysOfWeek, Long coverId, String color, Boolean isDeleted) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
        this.coverId = coverId;
        this.color = color;
        this.isDeleted = isDeleted;
    }

    public static Project of(ProjectCreateUpdateRequest request, Long coverId) {
        return Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate()).endDate(request.getEndDate())
                .startTime(request.getStartTime()).endTime(request.getEndTime())
                .daysOfWeek(request.getDaysOfWeek())
                .color(request.getColor())
                .coverId(coverId)
                .isDeleted(false)
                .build();
    }

    public void updateFrom(ProjectCreateUpdateRequest request, Long coverId) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.daysOfWeek = request.getDaysOfWeek();
        this.color = request.getColor();
        this.coverId = coverId;
    }

    public void deleteProject() {
        this.isDeleted = true;
    }

}
