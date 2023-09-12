package com.tomato.backend.controller;


/*
import com.tomato.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService; // 서비스 주입

    // 이미지를 업로드하고 처리된 이미지를 반환하는 API
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        String processedImage = imageService.processImage(file); // 이미지 처리
        return ResponseEntity.ok(processedImage); // 처리된 이미지를 base64 형태로 반환
    }
}
*/


import com.tomato.backend.dto.ImageResponse;
import com.tomato.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;





    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        return imageService.uploadAndConvertImage(file);
    }


    /*
        @PostMapping("/upload")
        public String uploadImage(@RequestParam("url") String url){

            return imageService.uploadAndConvertImage(url);

        }

      */
    @GetMapping("/images")
    public List<ImageResponse> getAllImages() {
        return imageService.getAllImages();
    }

    @GetMapping("/images/{id}")
    public ImageResponse getImageById(@PathVariable Long id) {
        return imageService.getImageById(id);
    }


    @DeleteMapping("/images/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }




}


