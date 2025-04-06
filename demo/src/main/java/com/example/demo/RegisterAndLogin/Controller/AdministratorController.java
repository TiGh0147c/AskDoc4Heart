package com.example.demo.RegisterAndLogin.Controller;

import com.example.demo.RegisterAndLogin.Entity.Administrator;
import com.example.demo.RegisterAndLogin.Entity.ResponseMessage;
import com.example.demo.RegisterAndLogin.Entity.dto.AdministratorDTO;
import com.example.demo.RegisterAndLogin.Service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/administrator")
public class AdministratorController {

    @Autowired
    IAdminService adminService;

    //注册
    @PostMapping
    public ResponseMessage<Administrator> admin_register(@RequestBody AdministratorDTO administrator){
        Administrator admin=adminService.register(administrator);
        return ResponseMessage.success(admin);
    }

    //登录
    @GetMapping
    public ResponseMessage user_login(@RequestParam String username, @RequestParam String password){
        Optional<Administrator> newAdministrator=adminService.login(username,password);
        return ResponseMessage.success(newAdministrator);
    }
}
