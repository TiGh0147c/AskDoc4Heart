package com.example.demo.profilemanagement.mapper;

import com.example.demo.profilemanagement.entity.Counselor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CounselorMapper {

    // 根据咨询师ID查询咨询师信息
    Counselor getCounselorById(Integer counselorId);

    // 更新咨询师信息
    int updateCounselorProfile(Counselor counselor);

    // 更新督导信息（该咨询师是督导）
    void updateSupervisorProfile(Counselor counselor);
}
