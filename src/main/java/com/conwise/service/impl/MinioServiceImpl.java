package com.conwise.service.impl;

import com.conwise.service.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    // 使用构造函数注入配置值
    public MinioServiceImpl(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.credentials.username}") String username,
            @Value("${minio.credentials.password}") String password,
            @Value("${minio.bucketName}") String bucketName
    ) {
        this.bucketName = bucketName;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }
    // 上传表单提交的数据文件
    @Override
    public String uploadFile(MultipartFile file, String newFileName) throws Exception {
        // 生成唯一文件名
        if (newFileName.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".unknown";
            newFileName = UUID.randomUUID().toString() + fileExtension;
        }
        // 上传文件到 MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(newFileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return newFileName;
    }

    // 重载方法，上传 resources 目录中的文件
    public void uploadFile(String resourcePath, String newFileName) throws Exception {
        // 从 resources 目录读取文件
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            throw new Exception("资源文件未找到: " + resourcePath);
        }

        // 生成唯一文件名
        if (newFileName.isEmpty()) {
            String originalFilename = resource.getFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".unknown";
            newFileName = UUID.randomUUID().toString() + fileExtension;
        }

        // 上传文件到 MinIO
        try (InputStream inputStream = resource.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newFileName)
                            .stream(inputStream, resource.contentLength(), -1)
                            .contentType("image/png")
                            .build()
            );
        }
    }
    // 删除 MinIO 中的指定文件
    @Override
    public void deleteFile(String fileName) throws Exception {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new Exception("删除文件失败: " + fileName, e);
        }
    }

    // 根据文件名确定 ContentType
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}