package com.example.demo.profilemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户资料修改审核记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModificationAudit {
    private Integer auditId;
    private Integer userId;
    private String modifyField; // "nickname" or "avatar"
    private String newValue;
    private String auditStatus; // "pending", "approved", "rejected"
}
