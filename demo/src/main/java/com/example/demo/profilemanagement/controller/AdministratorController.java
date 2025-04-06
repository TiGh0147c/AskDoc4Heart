package com.example.demo.profilemanagement.controller;

import com.example.demo.profilemanagement.dto.AdministratorProfileModificationDTO;
import com.example.demo.profilemanagement.entity.Administrator;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import com.example.demo.profilemanagement.service.AdministratorService;
import com.example.demo.profilemanagement.service.UserAuditService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile-management/administrator")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private UserAuditService userAuditService;


    @GetMapping("/profile")
    public ResponseEntity<Administrator> getAdministratorProfile(@RequestParam Integer adminId) {
        Administrator administrator = administratorService.getAdministratorById(adminId);
        if (administrator == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(administrator);
    }
    // 修改管理员信息
    @PostMapping(value = "/modification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifyProfile(
            @Valid @ModelAttribute AdministratorProfileModificationDTO dto, // 校验普通字段
            BindingResult result,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {  // 接收头像文件

        // 如果校验失败，返回错误信息
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        int modifyResult = administratorService.modifyAdministratorProfile(dto, avatarFile);
        if (modifyResult == -1){
            return ResponseEntity.badRequest().body("管理员不存在");
        }

        return ResponseEntity.ok("管理员信息修改成功");
    }

    // 根据status（可不给）获取所有用户修改审核记录
    @GetMapping("/user-modification-audits")
    public List<UserModificationAudit> getAllUserModificationAudits(
            @RequestParam(required = false) String status // 可选参数：pending, approved, rejected
    ) {
        return userAuditService.getAuditsByStatus(status);
    }

    // 审核用户修改
    @PostMapping("/user-modification-audits/{auditId}/review")
    public ResponseEntity<String> reviewUserModification(
            @PathVariable Integer auditId,
            @RequestParam String decision // "approved" 或 "rejected"
    ) {
        boolean success = userAuditService.reviewAudit(auditId, decision);
        return success ? ResponseEntity.ok("审核成功") : ResponseEntity.status(400).body("审核失败");
    }
}
