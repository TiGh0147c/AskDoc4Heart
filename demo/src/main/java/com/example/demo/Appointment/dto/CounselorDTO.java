package com.example.demo.Appointment.dto;
import com.example.demo.Appointment.entity.Counselor;
import jakarta.persistence.Id;
import lombok.Data;

@Data // Lombok注解，自动生成getter/setter
public class CounselorDTO {
    private Integer counselorId; // 咨询师ID
    private String name; // 咨询师姓名
    private String avatar; // 咨询师头像
    private String phoneNumber; // 咨询师手机号
    private String email; // 咨询师邮箱
    private Counselor.Gender gender; // 咨询师性别
    private String expertiseArea; // 咨询师专长领域
    private Boolean isSupervisor; // 是否为
    private Counselor.Status status; // 咨询师状态（available/unavailable）
    private Boolean onDuty; // 是否在职

    // 构造函数
    public CounselorDTO(Integer counselorId, String name, String avatar, String phoneNumber,
                        String email, Counselor.Gender gender, String expertiseArea, Boolean isSupervisor,
                        Counselor.Status status, Boolean onDuty) {
        this.counselorId = counselorId;
        this.name = name;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.expertiseArea = expertiseArea;
        this.isSupervisor = isSupervisor;
        this.status = status;
        this.onDuty = onDuty;
    }
}
