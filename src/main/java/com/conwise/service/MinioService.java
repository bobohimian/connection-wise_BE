package com.conwise.service;

import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MinioService {
    public String uploadFile(MultipartFile file,String newFileName) throws Exception;
    public void uploadFile(String path,String newFileName) throws Exception;

}
