package com.example.demo.profilemanagement.controller;

import com.example.demo.profilemanagement.dto.CounselorProfileModificationDTO;
import com.example.demo.profilemanagement.dto.UserProfileModificationDTO;
import com.example.demo.profilemanagement.entity.Counselor;
import com.example.demo.profilemanagement.service.CounselorService;
import com.example.demo.profilemanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("counselorProfileController")
@RequestMapping("/api/profile-management/counselor")
public class CounselorController {

    @Autowired
    private CounselorService counselorService;

    // 获取咨询师信息
    @GetMapping("/profile")
    public ResponseEntity<Counselor> getCounselorProfile(@RequestParam Integer counselorId) {
        Counselor counselor = counselorService.getCounselorById(counselorId);
        if (counselor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(counselor);
    }

    // 修改咨询师信息
    @PostMapping(value = "/modification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifyProfile(
            @Valid @ModelAttribute CounselorProfileModificationDTO dto, // 校验普通字段
            BindingResult result,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {  // 接收头像文件

        // 如果校验失败，返回错误信息
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        int modifyResult = counselorService.modifyCounselorProfile(dto, avatarFile);
        if (modifyResult == -1){
            return ResponseEntity.badRequest().body("咨询师不存在");
        }

        return ResponseEntity.ok("咨询师信息修改成功");
    }

}
