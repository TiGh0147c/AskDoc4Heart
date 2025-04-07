package com.example.demo.profilemanagement.service.impl;

import com.example.demo.profilemanagement.dto.UserProfileModificationDTO;
import com.example.demo.profilemanagement.entity.User;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import com.example.demo.profilemanagement.mapper.UserMapper;
import com.example.demo.profilemanagement.service.FileStorageService;
import com.example.demo.profilemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileStorageService fileStorageService;

    // 修改用户信息
    @Override
    public int modifyUserProfile(UserProfileModificationDTO dto, MultipartFile avatarFile) {

        // 根据 userId 查找用户并修改
        if (dto.getUserId() == null) {
            return -1; // 没给用户ID
        }
        User user = userMapper.getUserById(dto.getUserId());
        if (user == null) {
            return -1; // 用户不存在
        }

        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 保存文件并获取文件名
            String avatarFileName = fileStorageService.saveAvatarFile(avatarFile);
//            // 更新头像字段
//            dto.setAvatar("avatar/" + avatarFileName);

            String avatarPath = "avatar/" + avatarFileName;
            // 写入审核表，不直接修改
            UserModificationAudit audit = new UserModificationAudit();
            audit.setUserId(dto.getUserId());
            audit.setModifyField("avatar");
            audit.setNewValue(avatarPath);
            userMapper.insertUserModificationAudit(audit);
        }

        if (dto.getNickname() != null) {
            UserModificationAudit audit = new UserModificationAudit();
            audit.setUserId(dto.getUserId());
            audit.setModifyField("nickname");
            audit.setNewValue(dto.getNickname());
            userMapper.insertUserModificationAudit(audit);
        }

        // 只更新非空字段，避免修改没有传递的属性
//        if (dto.getNickname() != null) {
//            user.setNickname(dto.getNickname());
//        }

//        if (dto.getAvatar() != null) {
//            user.setAvatar(dto.getAvatar());  // 头像字段
//        }

        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }

        if (dto.getOccupation() != null) {
            user.setOccupation(dto.getOccupation());
        }

        // 保存修改后的用户数据
        return userMapper.updateUserProfile(user);
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.getUserById(userId);
    }
}
