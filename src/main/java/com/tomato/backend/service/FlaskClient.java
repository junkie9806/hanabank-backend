package com.tomato.backend.service;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FlaskClient {

    private final RestTemplate restTemplate;

    public FlaskClient() {
        this.restTemplate = new RestTemplate();
    }

    //----------------------------------------------------------------
    // 사진파일 형태로 flask에 전달
    public File convertImage(MultipartFile originalFile) {
        try {
            // MultipartFile을 File로 변환
            File convFile = new File(originalFile.getOriginalFilename());
            convFile.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(originalFile.getBytes());
            }

            // Flask에 보낼 요청 생성
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            Resource resource = new FileSystemResource(convFile);
            body.add("image", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Flask에 POST 요청을 보내 변환된 이미지를 받음
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    "http://your_flask_server/convert",
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            byte[] result = response.getBody();

            if (result == null) {
                throw new RuntimeException("변환된 이미지를 받아오지 못했습니다.");
            }

            // 변환된 이미지를 임시 파일로 저장
            File convertedFile = File.createTempFile("converted-", ".jpg"); // 확장자는 실제 파일에 따라 수정
            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(result);
            }

            return convertedFile;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 변환 중 오류가 발생했습니다.");
        }
    }

/*

    //url형태로 flask에 전달
    public String convertImage(String originalImageUrl) {
        RestTemplate restTemplate = new RestTemplate();
        String flaskApiUrl = "http://flask-app:5000/convert"; // Flask 서비스의 URL과 포트 번호를 입력하세요

        String convertedImageUrl = restTemplate.postForObject(flaskApiUrl, originalImageUrl, String.class);

        // 변환된 이미지의 S3 URL을 반환합니다.
        return "https://tomatobackend.s3.amazonaws.com/" + convertedImageUrl;
    }

    */



}
