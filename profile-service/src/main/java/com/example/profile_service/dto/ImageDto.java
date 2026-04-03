package com.example.profile_service.dto;

import lombok.Data;

@Data
public class ImageDto {
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;

}
