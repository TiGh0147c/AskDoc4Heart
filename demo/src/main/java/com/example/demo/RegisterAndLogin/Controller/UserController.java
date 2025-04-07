package com.example.demo.RegisterAndLogin.Controller;

import com.example.demo.RegisterAndLogin.Entity.ResponseMessage;
import com.example.demo.RegisterAndLogin.Entity.User;
import com.example.demo.RegisterAndLogin.Entity.dto.UserDTO;
import com.example.demo.RegisterAndLogin.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    //注册
    @PostMapping
    public ResponseMessage<User> user_register(@RequestBody UserDTO user){
        User newUser=userService.register(user);
        return ResponseMessage.success(newUser);
    }

    //登录
    @GetMapping
    public ResponseMessage user_login(@RequestParam String username, @RequestParam String password){
        Optional<User> newUser=userService.login(username,password);
        return ResponseMessage.success(newUser);
    }
}