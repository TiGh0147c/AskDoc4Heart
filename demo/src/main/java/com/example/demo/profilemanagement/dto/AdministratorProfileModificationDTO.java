package com.example.demo.profilemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员资料修改，用户名，创建时间没包含，因为不可修改
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdministratorProfileModificationDTO {
    private Integer administratorId;

    @Size(min = 6, message = "密码长度必须大于等于6个字符")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$", message = "密码只能包含英文字符、数字或符号")
    private String password;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,6}$", message = "姓名必须为2~6个汉字")
    private String realName;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactNumber;

    private String avatar;

    @Pattern(regexp = "^(active|disabled)$", message = "状态必须是 active 或 disabled")
    private String status;

}
