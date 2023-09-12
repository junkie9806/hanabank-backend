
package com.tomato.backend.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Uploader {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String fileName = dirName + "/" + UUID.randomUUID() + originalFileName;
        InputStream inputStream = multipartFile.getInputStream();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());

        amazonS3.putObject(bucket, fileName, inputStream, objectMetadata);

        return "https://tomatobackend.s3.amazonaws.com/" + fileName;
    }

    public void delete(String fileUrl) {
        String fileName = fileUrl.replace("https://tomatobackend.s3.amazonaws.com/", "");
        amazonS3.deleteObject(bucket, fileName);
    }



}
