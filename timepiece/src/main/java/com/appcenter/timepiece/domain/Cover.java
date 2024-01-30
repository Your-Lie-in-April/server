package com.appcenter.timepiece.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Cover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String color;
    @Column(name="cover_image")
    private String coverImage;
    @OneToMany(mappedBy = "cover")
    private List<Project> projects = new ArrayList<>();
}
