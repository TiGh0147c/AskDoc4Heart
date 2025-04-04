package com.example.demo.binding.dto;

import com.example.demo.binding.entity.Counselor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CounselorDTO {
    private Integer counselorId;
    private String name;
    private String phoneNumber;
    private String statusMessage;  // 状态消息字段
    public CounselorDTO(Counselor counselor) {
        this.counselorId = counselor.getCounselorId();
        this.name = counselor.getName();
        this.phoneNumber = counselor.getPhoneNumber();
        this.statusMessage = "Counselor found";
    }

    public CounselorDTO() {
        this.statusMessage = "Counselor not found";  // 默认状态消息
    }
}
