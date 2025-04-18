package com.example.demo.RegisterAndLogin.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Counselor")
public class Counselor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int counselor_id;
    private String name;
    private String password;
    private String phone_number;
    private String email;
    private Boolean is_supervisor;
    private String expertise_Area;
    private String gender;
    private String avatar;
    

    public int getCounselor_id() {
        return counselor_id;
    }

    public void setCounselor_id(int counselor_id) {
        this.counselor_id = counselor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIs_supervisor() {
        return is_supervisor;
    }

    public void setIs_supervisor(Boolean is_supervisor) {
        this.is_supervisor = is_supervisor;
    }

    public String getExpertise_Area() {
        return expertise_Area;
    }

    public void setExpertise_Area(String expertise_Area) {
        this.expertise_Area = expertise_Area;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Counselor{" +
                "counselor_id=" + counselor_id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", is_supervisor=" + is_supervisor +
                ", expertise_Area='" + expertise_Area + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
