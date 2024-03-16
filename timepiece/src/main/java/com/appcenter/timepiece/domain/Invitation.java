package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String url;

    private Invitation(Project project, String url) {
        this.project = project;
        this.url = url;
    }

    public static Invitation of(Project project, String url) {
        return new Invitation(project, url);
    }
}
