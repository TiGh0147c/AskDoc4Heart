package com.example.demo.profilemanagement.mapper;

import com.example.demo.profilemanagement.entity.User;
import com.example.demo.profilemanagement.entity.UserModificationAudit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // 根据用户ID查询用户信息
    User getUserById(Integer userId);

    // 更新用户信息
    int updateUserProfile(User user);

    // 插入用户修改审核记录
    int insertUserModificationAudit(UserModificationAudit audit);

    // 更新用户昵称
    int updateNickname(Integer userId, String nickname);

    // 更新用户头像
    int updateAvatar(Integer userId, String avatar);

}
