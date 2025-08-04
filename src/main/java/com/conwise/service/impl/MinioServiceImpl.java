package com.conwise.service.impl;

import com.conwise.service.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
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
        log.info("endpoint: " + endpoint);
        log.info("username: " + username);
        log.info("password: " + password);
        log.info("bucketName: " + bucketName);
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
            newFileName = UUID.randomUUID() + fileExtension;
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
    public void uploadFileSystemFile(String filePath, String newFileName) throws Exception {
        // 1. 检查文件系统路径是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("文件未找到: " + filePath);
        }
        if (!file.isFile()) {
            throw new Exception("路径不是有效文件: " + filePath);
        }

        // 2. 生成唯一文件名（保留原逻辑）
        if (newFileName == null || newFileName.isEmpty()) {
            String originalFilename = file.getName();  // 从 File 对象获取原始文件名
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);  // 提取扩展名（含点）
            } else {
                fileExtension = ".unknown";  // 无扩展名时默认
            }
            newFileName = UUID.randomUUID() + fileExtension;
        }

        // 3. 读取文件流并上传到 MinIO
        try (InputStream inputStream = new FileInputStream(file)) {  // 使用文件输入流
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newFileName)
                            .stream(inputStream, file.length(), -1)  // 使用文件大小（file.length()）
                            .contentType(determineContentType(newFileName))  // 保留原方法
                            .build()
            );
        } catch (Exception e) {
            throw new Exception("上传失败: " + e.getMessage(), e);
        }

        log.info("uploadFileSystemFile 成功，新文件名: {}", newFileName);
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

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<String> uploadFileAsync(MultipartFile file, String newFileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.uploadFile(file, newFileName);
                return newFileName;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Void> uploadFileSystemFileAsync(String path, String newFileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.uploadFileSystemFile(path, newFileName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<Void> deleteFileAsync(String path) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.deleteFile(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 根据文件名确定 ContentType
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png"))
            return "image/png";
        else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}