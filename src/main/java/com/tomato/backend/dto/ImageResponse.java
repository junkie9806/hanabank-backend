package com.tomato.backend.dto;

import com.tomato.backend.domain.ImageEntity;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter @Setter
public class ImageResponse {

    private Long id;
    private String originalUrl;
    private String convertedUrl;

    public ImageResponse(ImageEntity image) {
        this.id = image.getId();
        this.originalUrl = image.getOriginalImageUrl();
        this.convertedUrl = image.getConvertedImageUrl();
    }

    // Getter, Setter

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getConvertedUrl() {
        return convertedUrl;
    }
}
