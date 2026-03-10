package com.example.Yuhbaek.service.MyPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * S3에 파일 업로드
     */
    public String uploadFile(MultipartFile file, String dirName) {
        String fileName = createFileName(file.getOriginalFilename(), dirName);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // S3 URL 생성
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucket, region, fileName);

            log.info("S3 파일 업로드 성공: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw new IllegalArgumentException("파일 업로드에 실패했습니다");
        }
    }

    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공: {}", fileName);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage());
        }
    }

    /**
     * 고유한 파일명 생성
     */
    private String createFileName(String originalFileName, String dirName) {
        return dirName + "/" + UUID.randomUUID() + "-" + originalFileName;
    }

    /**
     * URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String fileUrl) {
        try {
            // https://bucket-name.s3.region.amazonaws.com/dirName/filename.jpg
            // → dirName/filename.jpg 추출
            String[] parts = fileUrl.split(".amazonaws.com/");
            return parts.length > 1 ? parts[1] : fileUrl;
        } catch (Exception e) {
            log.error("파일명 추출 실패: {}", e.getMessage());
            return fileUrl;
        }
    }
}