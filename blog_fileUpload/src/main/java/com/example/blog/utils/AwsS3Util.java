package com.example.blog.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j(topic = "AwsS3Util")
@Component
public class AwsS3Util {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AwsS3Util(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadImgFile(MultipartFile multipartFile, String dirName) {

        String imgFileName = dirName + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        ObjectMetadata objectMeta = new ObjectMetadata();

        try {
            objectMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3.putObject(bucket, imgFileName, multipartFile.getInputStream(), objectMeta);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        URL url = amazonS3.getUrl(bucket, imgFileName);
        return url.toString();
    }

}
