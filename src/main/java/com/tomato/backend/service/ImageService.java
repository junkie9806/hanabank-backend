package com.tomato.backend.service;

import com.tomato.backend.domain.ImageEntity;
import com.tomato.backend.dto.ImageResponse;
import com.tomato.backend.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private FlaskClient flaskClient;



    //s3에 이미지 저장 메서드
    public ImageEntity saveOriginalImage(MultipartFile originalFile) {
        try {
            String imageUrl = s3Uploader.upload(originalFile, "original");
            ImageEntity imageEntity = new ImageEntity(imageUrl, null);
            return imageRepository.save(imageEntity);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 중 오류 발생");
        }
    }

    public MultipartFile convertFileToMultipartFile(File file) {
        try {
            Path path = file.toPath();
            String name = file.getName();
            String originalFileName = file.getName();
            String contentType = Files.probeContentType(path);
            byte[] content = Files.readAllBytes(path);

            MultipartFile multipartFile = new MockMultipartFile(
                    name,
                    originalFileName,
                    contentType,
                    new FileInputStream(file)
            );

            return multipartFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //flask에 이미지 전달하여 변환된 이미지 전달받고 파일 및 url 저장
    public ImageEntity saveAndProcessImage(MultipartFile originalFile) {
        try {
            // 원본 이미지 저장 -> s3에 저장
            ImageEntity imageEntity = saveOriginalImage(originalFile);

            // Flask로 이미지 처리 요청 및 변환된 이미지 파일 받기
            File convertedImageFile = flaskClient.convertImage(originalFile);

            // File을 MultipartFile로 변환
            MultipartFile convertedMultipartFile = convertFileToMultipartFile(convertedImageFile);

            // 변환된 이미지 S3에 저장
            String convertedImageUrl = s3Uploader.upload(convertedMultipartFile, "converted");

            // DB에 변환된 이미지 URL 저장
            imageEntity.setConvertedImageUrl(convertedImageUrl);
            return imageRepository.save(imageEntity);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 또는 변환 중 오류가 발생했습니다.");
        }
    }




    public String uploadAndConvertImage(MultipartFile file) {
        ImageEntity imageEntity = saveAndProcessImage(file);
        return imageEntity.getOriginalImageUrl();  // 필요한 경우, 변환된 이미지 URL도 반환 가능
    }



/*
    public ImageEntity saveAndProcessImage(String originalurl){

        ImageEntity imageEntity = new ImageEntity();


        imageEntity.setConvertedImageUrl(flaskClient.convertImage(originalurl));

        return imageRepository.save(imageEntity);


    }

    public String uploadAndConvertImage(String convertedImageUrl)
    {
        ImageEntity imageEntity = saveAndProcessImage(convertedImageUrl);
        return imageEntity.getOriginalImageUrl();
    }

 */

    public List<ImageResponse> getAllImages() {
        return imageRepository.findAll().stream()
                .map(ImageResponse::new)
                .collect(Collectors.toList());
    }

    public ImageResponse getImageById(Long id) {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id=" + id));
        return new ImageResponse(imageEntity);
    }

    public void deleteImageById(Long id) {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id=" + id));
        s3Uploader.delete(imageEntity.getOriginalImageUrl());
        imageRepository.deleteById(id);
    }
}
