package com.example.profile_service.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images_profile")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;

    @Lob
    @JsonIgnore
    private byte[] bytes;


    @OneToOne(mappedBy = "avatar")
    @JsonIgnore
    private Profile profile;

}
