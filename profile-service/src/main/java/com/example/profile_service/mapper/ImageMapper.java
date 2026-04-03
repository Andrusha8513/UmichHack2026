package com.example.profile_service.mapper;

import com.example.profile_service.dto.ImageDto;
import com.example.profile_service.entity.Image;
import org.springframework.stereotype.Component;


@Component
public class ImageMapper {
    public ImageDto toDto(Image image){
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setName(image.getName());
        imageDto.setSize(image.getSize());
        imageDto.setOriginalFileName(image.getOriginalFileName());
        imageDto.setContentType(image.getContentType());
        return imageDto;
    }

}
