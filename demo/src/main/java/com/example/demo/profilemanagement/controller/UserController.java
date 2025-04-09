package com.example.demo.profilemanagement.controller;

import com.example.demo.profilemanagement.dto.UserProfileModificationDTO;
import com.example.demo.profilemanagement.entity.User;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import com.example.demo.profilemanagement.service.UserAuditService;
import com.example.demo.profilemanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController("userProfileController")
@RequestMapping("/api/profile-management/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserAuditService userAuditService;

    // 获取用户信息
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestParam Integer userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    // 修改用户信息
    @PostMapping(value = "/modification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifyProfile(
            @Valid @ModelAttribute UserProfileModificationDTO dto, // 校验普通字段
            BindingResult result,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {  // 接收头像文件

        // 如果校验失败，返回错误信息
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        int modifyResult = userService.modifyUserProfile(dto, avatarFile);
        if (modifyResult == -1){
            return ResponseEntity.badRequest().body("用户不存在");
        }

        return ResponseEntity.ok("用户信息修改成功");
    }

    // 修改用户信息，使用JSON格式（专门给小程序提供，不包括头像文件）
    @PostMapping("/modification/json")
    public ResponseEntity<?> modifyProfileJson(
            @RequestBody @Valid UserProfileModificationDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        int modifyResult = userService.modifyUserProfile(dto, null); // 不传头像
        if (modifyResult == -1){
            return ResponseEntity.badRequest().body("用户不存在");
        }

        return ResponseEntity.ok("用户信息修改成功");
    }

    // 单独上传用户头像（小程序）
    @PostMapping("/modification/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("avatarFile") MultipartFile avatarFile,
            @RequestParam("userId") Integer userId) {

        if (avatarFile == null || avatarFile.isEmpty()) {
            return ResponseEntity.badRequest().body("头像文件不能为空");
        }

        boolean result = userService.updateUserAvatar(userId, avatarFile);
        if (!result) {
            return ResponseEntity.badRequest().body("用户不存在或头像上传失败");
        }

        return ResponseEntity.ok("头像上传成功");
    }

    // 获取用户修改的审核结果
    @GetMapping("/modification-audits")
    public List<UserModificationAudit> getUserModificationAudits(
            @RequestParam Integer userId,
            @RequestParam(required = false) String status
    ) {
        return userAuditService.getAuditsByUserIdAndStatus(userId, status);
    }
}
