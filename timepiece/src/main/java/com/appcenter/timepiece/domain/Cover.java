package com.appcenter.timepiece.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="cover_image_url")
    private String coverImageUrl;

    @OneToMany(mappedBy = "cover")
    private List<Project> projects = new ArrayList<>();
}
