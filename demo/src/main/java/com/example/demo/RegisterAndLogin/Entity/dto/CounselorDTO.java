package com.example.demo.RegisterAndLogin.Entity.dto;

public class CounselorDTO {
    private String name;
    private String gender;
    private String password;
    private String phone_number;
    private String email;
    private Boolean is_supervisor;
    private String expertise_Area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
        return "CounselorDTO{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", is_supervisor=" + is_supervisor +
                ", expertise_Area='" + expertise_Area + '\'' +
                '}';
    }
}
