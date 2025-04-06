package com.example.demo.profilemanagement.service.impl;

import com.example.demo.profilemanagement.entity.UserModificationAudit;
import com.example.demo.profilemanagement.mapper.UserModificationAuditMapper;
import com.example.demo.profilemanagement.mapper.UserMapper;
import com.example.demo.profilemanagement.service.UserAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAuditServiceImpl implements UserAuditService {

    @Autowired
    private UserModificationAuditMapper auditMapper;

    @Autowired
    private UserMapper userMapper;

    // 获取用户修改审核记录
    @Override
    public List<UserModificationAudit> getAuditsByStatus(String status) {
        if (status == null) {
            return auditMapper.selectAll();
        } else {
            return auditMapper.selectByStatus(status);
        }
    }

    // 审核用户修改
    @Override
    public boolean reviewAudit(Integer auditId, String decision) {
        UserModificationAudit audit = auditMapper.selectById(auditId);
        if (audit == null || (!"approved".equals(decision) && !"rejected".equals(decision))) {
            return false; // 无效的审核ID或决策
        }

        // 更新审核状态
        audit.setAuditStatus(decision);
        auditMapper.updateAuditStatus(audit);

        // 如果通过，更新对应用户字段
        if ("approved".equals(decision)) {
            if ("nickname".equals(audit.getModifyField())) {
                userMapper.updateNickname(audit.getUserId(), audit.getNewValue());
            } else if ("avatar".equals(audit.getModifyField())) {
                userMapper.updateAvatar(audit.getUserId(), audit.getNewValue());
            }
        }

        return true;
    }


    // 获取用户审核记录
    @Override
    public List<UserModificationAudit> getAuditsByUserIdAndStatus(Integer userId, String status) {
        if (status == null) {
            return auditMapper.getAuditsByUserId(userId);
        } else {
            return auditMapper.getAuditsByUserIdAndStatus(userId, status);
        }
    }


}
