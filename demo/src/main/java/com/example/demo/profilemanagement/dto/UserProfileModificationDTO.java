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
 * 普通用户资料修改，手机号，open_id，union_id没包含，因为不可修改
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileModificationDTO {
    private Integer userId;
    @Size(min = 2, max = 32, message = "昵称长度必须在2到32个字符之间")
    @Pattern(
            regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\- ]{2,32}$",
            message = "昵称不能包含特殊字符，如\\ ; ! @ # $ % ^ & * ( )"
    )
    private String nickname;

    private String avatar; // 用于接收头像文件

    @Pattern(regexp = "male|female|other|unknown", message = "性别非法")
    private String gender;

    @Past(message = "生日必须是过去的日期")
    private LocalDate birthday;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 6, message = "密码长度必须大于等于6个字符")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$", message = "密码只能包含英文字符、数字或符号")
    private String password;

    @Size(max = 50, message = "职业不能超过50字符")
    private String occupation;

}
