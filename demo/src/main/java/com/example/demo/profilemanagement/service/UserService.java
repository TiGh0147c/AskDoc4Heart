package com.example.demo.profilemanagement.service;

import com.example.demo.profilemanagement.dto.UserProfileModificationDTO;
import com.example.demo.profilemanagement.entity.User;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService {
    // 修改用户资料
    int modifyUserProfile(UserProfileModificationDTO dto, MultipartFile avatarFile);

    // 根据用户ID获取用户信息
    User getUserById(Integer userId);
}
