package com.example.demo.profilemanagement.service;

import com.example.demo.profilemanagement.dto.CounselorProfileModificationDTO;
import com.example.demo.profilemanagement.dto.UserProfileModificationDTO;
import com.example.demo.profilemanagement.entity.Counselor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CounselorService {
    // 修改咨询师信息
    int modifyCounselorProfile(CounselorProfileModificationDTO dto, MultipartFile avatarFile);

    // 获取咨询师信息
    Counselor getCounselorById(Integer counselorId);
}
