package com.example.demo.profilemanagement.service;

import com.example.demo.profilemanagement.dto.AdministratorProfileModificationDTO;
import com.example.demo.profilemanagement.entity.Administrator;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface AdministratorService {
    // 修改管理员信息
    int modifyAdministratorProfile(AdministratorProfileModificationDTO dto, MultipartFile avatarFile);

    // 根据管理员ID获取管理员信息
    Administrator getAdministratorById(Integer adminId);
}
