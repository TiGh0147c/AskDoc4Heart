package com.example.demo.Appointment.entity;//import javax.persistence.*;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data // 自动生成 getter 和 setter

public class Counselor {

    private Integer counselorId; // 咨询师ID，主键

    private String name; // 咨询师姓名

    private String avatar; // 咨询师头像

    private String phoneNumber; // 咨询师手机号

    private String password; // 咨询师密码

    private String email; // 咨询师邮箱

    private Gender gender; // 咨询师性别

    private String counselorCertificate; // 咨询师证书

    private Boolean isSupervisor; // 是否为主管

    private Status status; // 咨询师状态（available/unavailable）

    private String expertiseArea; // 咨询师专长领域

    private Boolean onDuty; // 是否在职

    // Gender 枚举类型
    public enum Gender {
        male, female, other, unknown
    }

    // Status 枚举类型
    public enum Status {
        unavailable, available
    }
}
