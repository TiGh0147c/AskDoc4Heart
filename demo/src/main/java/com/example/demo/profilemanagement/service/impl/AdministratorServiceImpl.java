package com.example.demo.profilemanagement.service.impl;

import com.example.demo.profilemanagement.dto.AdministratorProfileModificationDTO;
import com.example.demo.profilemanagement.entity.Administrator;
import com.example.demo.profilemanagement.entity.Counselor;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import com.example.demo.profilemanagement.mapper.AdministratorMapper;
import com.example.demo.profilemanagement.service.AdministratorService;
import com.example.demo.profilemanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class AdministratorServiceImpl implements AdministratorService {

    @Autowired
    private AdministratorMapper administratorMapper;
    @Autowired
    private FileStorageService fileStorageService;

    // 修改管理员信息
    @Override
    public int modifyAdministratorProfile(AdministratorProfileModificationDTO dto, MultipartFile avatarFile) {

        // 根据 administratorId 查找管理员并修改
        if (dto.getAdministratorId() == null) {
            return -1; // 没给管理员ID
        }
        Administrator administrator = administratorMapper.getAdministratorById(dto.getAdministratorId());
        if (administrator == null) {
            return -1; // 管理员不存在
        }

        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 保存文件并获取文件名
            String avatarFileName = fileStorageService.saveAvatarFile(avatarFile);
            // 更新头像字段
            dto.setAvatar("avatar/" + avatarFileName);
        }

        // 只更新非空字段，避免修改没有传递的属性
        if (dto.getPassword() != null) {
            administrator.setPassword(dto.getPassword()); // 头像字段
        }

        if (dto.getRealName() != null) {
            administrator.setRealName(dto.getRealName());
        }

        if (dto.getEmail() != null) {
            administrator.setEmail(dto.getEmail());
        }

        if (dto.getContactNumber() != null) {
            administrator.setContactNumber(dto.getContactNumber());
        }

        if (dto.getAvatar() != null) {
            administrator.setAvatar(dto.getAvatar());
        }

        if (dto.getStatus() != null) {
            administrator.setStatus(dto.getStatus());
        }

        // 保存修改后的用户数据
        return administratorMapper.updateAdministratorProfile(administrator);
    }


    // 根据管理员ID获取管理员信息
    @Override
    public Administrator getAdministratorById(Integer adminId) {
        return administratorMapper.getAdministratorById(adminId);
    }

}
