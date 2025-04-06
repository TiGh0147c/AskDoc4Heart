package com.example.demo.profilemanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {
    String saveAvatarFile(MultipartFile file);
}
