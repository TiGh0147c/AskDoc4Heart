package com.example.demo.profilemanagement.service;

import com.example.demo.profilemanagement.entity.UserModificationAudit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserAuditService {

    // 获取审核记录
    List<UserModificationAudit> getAuditsByStatus(String status);

    // 审核
    boolean reviewAudit(Integer auditId, String decision);

    // 获取用户审核记录
    List<UserModificationAudit> getAuditsByUserIdAndStatus(Integer userId, String status);
}
