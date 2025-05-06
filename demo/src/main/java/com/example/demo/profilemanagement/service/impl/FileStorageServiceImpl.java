package com.example.demo.profilemanagement.service.impl;

import com.example.demo.profilemanagement.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    // 获取项目的根目录路径
    String projectRoot = System.getProperty("user.dir");
    // 设置头像存储目录
    String avatarDirectory = projectRoot + "/uploads/avatar/";

    @Override
    public String saveAvatarFile(MultipartFile avatarFile)
    {
        // 确保目录存在
        try {
            Path directoryPath = Paths.get(avatarDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("创建目录失败", e);
        }
        
        // 生成文件名
        String avatarFileName = System.currentTimeMillis() + "-" + avatarFile.getOriginalFilename();
        try {
            // 保存文件到指定路径
            Path path = Paths.get(avatarDirectory, avatarFileName);
            Files.copy(avatarFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        return avatarFileName;
    }
}
