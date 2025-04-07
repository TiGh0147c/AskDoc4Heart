package com.example.demo.profilemanagement.service.impl;

import com.example.demo.profilemanagement.dto.CounselorProfileModificationDTO;
import com.example.demo.profilemanagement.entity.Counselor;
import com.example.demo.profilemanagement.mapper.CounselorMapper;
import com.example.demo.profilemanagement.service.CounselorService;
import com.example.demo.profilemanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class CounselorServiceImpl implements CounselorService {

    @Autowired
    private CounselorMapper counselorMapper;
    @Autowired
    private FileStorageService fileStorageService;

    // 修改咨询师信息
    @Override
    public int modifyCounselorProfile(CounselorProfileModificationDTO dto, MultipartFile avatarFile) {

        // 根据 counselorId 查找咨询师并修改
        if (dto.getCounselorId() == null) {
            return -1; // 没给咨询师ID
        }
        Counselor counselor = counselorMapper.getCounselorById(dto.getCounselorId());
        if (counselor == null) {
            return -1; // 咨询师不存在
        }

        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 保存文件并获取文件名
            String avatarFileName = fileStorageService.saveAvatarFile(avatarFile);
            // 更新头像字段
            dto.setAvatar("avatar/" + avatarFileName);
        }

        // 只更新非空字段，避免修改没有传递的属性
        if (dto.getAvatar() != null) {
            counselor.setAvatar(dto.getAvatar()); // 头像字段
        }

        if (dto.getPassword() != null) {
            counselor.setPassword(dto.getPassword());
        }

        if (dto.getEmail() != null) {
            counselor.setEmail(dto.getEmail());
        }

        if (dto.getGender() != null) {
            counselor.setGender(dto.getGender());
        }

        if (dto.getCounselorCertificate() != null) {
            counselor.setCounselorCertificate(dto.getCounselorCertificate());
        }

//        if (dto.getIsSupervisor() != null) {
//            counselor.setIsSupervisor(dto.getIsSupervisor());
//        }

        if (dto.getStatus() != null) {
            counselor.setStatus(dto.getStatus());
        }

        if (dto.getExpertiseArea() != null) {
            counselor.setExpertiseArea(dto.getExpertiseArea());
        }

        if (dto.getOnDuty() != null) {
            counselor.setOnDuty(dto.getOnDuty());
        }

        // 判断该咨询师是否是督导，如果是督导，更新督导表
        if (counselor.getIsSupervisor() != null && counselor.getIsSupervisor()) {
            // 更新对应的督导表
            counselorMapper.updateSupervisorProfile(counselor);
        }

        // 保存修改后的咨询师数据
        return counselorMapper.updateCounselorProfile(counselor);
    }

    // 获取咨询师信息
    @Override
    public Counselor getCounselorById(Integer counselorId) {
        return counselorMapper.getCounselorById(counselorId);
    }
}
