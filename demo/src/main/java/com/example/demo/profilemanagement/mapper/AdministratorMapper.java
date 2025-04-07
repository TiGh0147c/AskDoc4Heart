package com.example.demo.profilemanagement.mapper;

import com.example.demo.profilemanagement.entity.Administrator;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdministratorMapper {

    // 根据管理员ID查询管理员信息
    Administrator getAdministratorById(Integer administratorId);

    // 更新管理员信息
    int updateAdministratorProfile(Administrator administrator);
}
