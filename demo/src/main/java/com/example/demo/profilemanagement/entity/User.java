package com.example.demo.profilemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * 普通用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer userId;
    private String unionId;
    private String openId;
    private String nickname;
    private String avatar;
    private String gender;
    private LocalDate birthday;
    private String email;
    private String password;
    private String occupation;
    private String phoneNumber;

}
