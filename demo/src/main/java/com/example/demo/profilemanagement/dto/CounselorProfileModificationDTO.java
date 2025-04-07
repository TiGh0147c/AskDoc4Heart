package com.example.demo.profilemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 咨询师资料修改，手机号，姓名没包含，因为不可修改，是否是督导也未包含，暂时不允许咨询师或督导自己修改
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CounselorProfileModificationDTO {
    private Integer counselorId;

//    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,6}$", message = "姓名必须为2~6个汉字")
//    private String name;

    private String avatar;

//    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
//    private String phoneNumber;

    @Size(min = 6, message = "密码长度必须大于等于6个字符")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$", message = "密码只能包含英文字符、数字或符号")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "male|female|other|unknown", message = "性别非法")
    private String gender;

    private String counselorCertificate;
//    private Boolean isSupervisor;

    @Pattern(regexp = "^(available|unavailable)$", message = "状态必须是 available 或 unavailable")
    private String status;

    @Size(max = 100, message = "专业领域不能超过100字符")
    private String expertiseArea;

    private Boolean onDuty;
}
