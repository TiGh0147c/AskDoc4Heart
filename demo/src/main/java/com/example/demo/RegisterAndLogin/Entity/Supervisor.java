package com.example.demo.RegisterAndLogin.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Supervisor")
public class Supervisor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int supervisor_id;
    private String name;
    private String password;
    private String phone_number;
    private String email;
    private Boolean is_supervisor;
    private String expertise_Area;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;

    public int getSupervisor_id() {
        return supervisor_id;
    }

    public void setSupervisor_id(int supervisor_id) {
        this.supervisor_id = supervisor_id;
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

    @Override
    public String toString() {
        return "Supervisor{" +
                "supervisor_id=" + supervisor_id +
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
