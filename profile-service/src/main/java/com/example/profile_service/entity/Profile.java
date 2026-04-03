package com.example.profile_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profile")
@NoArgsConstructor
public class Profile {
    @Id
    @Column(name = "profile_id")
    private Long id;

    private String name;
    private String secondName;
    private String surName;
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "avatar_id")
    private Image avatar;


    @OneToMany(mappedBy = "owner" , fetch = FetchType.LAZY , orphanRemoval = true)
    private List<Pvz> pvzs = new ArrayList<>();

}
