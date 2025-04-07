package com.example.demo.profilemanagement.mapper;

import com.example.demo.profilemanagement.entity.UserModificationAudit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserModificationAuditMapper {
    // 查询所有记录
    List<UserModificationAudit> selectAll();

    // 根据状态查询记录
    List<UserModificationAudit> selectByStatus(String status);

    // 根据ID查询记录
    UserModificationAudit selectById(Integer auditId);

    // 更新审核状态
    int updateAuditStatus(UserModificationAudit audit);

    // 根据用户ID和状态查询记录
    List<UserModificationAudit> getAuditsByUserIdAndStatus(Integer userId, String status);

    // 根据用户ID查询记录
    List<UserModificationAudit> getAuditsByUserId(Integer userId);
}
