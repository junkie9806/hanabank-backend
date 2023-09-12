package com.tomato.backend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "images")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    private String originalImageUrl;

    @Getter @Setter
    private String convertedImageUrl;

    public ImageEntity(String originalImageUrl, String convertedImageUrl) {
        this.originalImageUrl = originalImageUrl;
        this.convertedImageUrl = convertedImageUrl;
    }
}
