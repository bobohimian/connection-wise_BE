package com.conwise.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface MinioService {
    String uploadFile(MultipartFile file, String newFileName) throws Exception;

    void uploadFileSystemFile(String path, String newFileName) throws Exception;

    void deleteFile(String fileName) throws Exception;

    CompletableFuture<String> uploadFileAsync(MultipartFile file, String newFileName);

    CompletableFuture<Void> uploadFileSystemFileAsync(String path, String newFileName);

    CompletableFuture<Void> deleteFileAsync(String path);

}
