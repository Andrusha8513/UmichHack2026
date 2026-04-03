package com.example.profile_service.service;

import com.example.profile_service.entity.Image;
import com.example.profile_service.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public Image createImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Файл должен быть изображением");
        }
        if (file.getSize() > 50_000_000) {
            throw new IllegalArgumentException("Размер файла не должен превышать 50 Мб");
        }

        Image image = new Image();
        image.setName(UUID.randomUUID().toString());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }
}