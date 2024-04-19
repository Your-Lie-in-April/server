package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    @CollectionTable(name="project_days_of_week",
            joinColumns = @JoinColumn(name= "project_id"))
    private Set<DayOfWeek> daysOfWeek;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<MemberProject> memberProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Invitation> invitations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_id")
    private Cover cover;

    private String color;

    @Builder(access = AccessLevel.PRIVATE)
    private Project(String title, String description,
                    LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
                    Set<DayOfWeek> daysOfWeek, List<MemberProject> memberProjects, List<Invitation> invitations,
                    Cover cover, String color) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
        this.memberProjects = memberProjects;
        this.invitations = invitations;
        this.cover = cover;
        this.color = color;
    }

    public static Project of(ProjectCreateUpdateRequest request, Cover cover) {
        return Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate()).endDate(request.getEndDate())
                .startTime(request.getStartTime()).endTime(request.getEndTime())
                .daysOfWeek(request.getDaysOfWeek())
                .color(request.getColor())
                .cover(cover)
                .build();
    }

    public void updateFrom(ProjectCreateUpdateRequest request, Cover cover) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.daysOfWeek = request.getDaysOfWeek();
        this.color = request.getColor();
        this.cover = cover;
    }
}
