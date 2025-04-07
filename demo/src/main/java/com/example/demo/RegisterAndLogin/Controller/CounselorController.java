package com.example.demo.RegisterAndLogin.Controller;

import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.ResponseMessage;
import com.example.demo.RegisterAndLogin.Entity.dto.CounselorDTO;
import com.example.demo.RegisterAndLogin.Service.CounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/counselor")
public class CounselorController {

    @Autowired
    private CounselorService counselorService;

    @PostMapping
    public ResponseMessage<Counselor> counselor_register(@RequestBody CounselorDTO counselor){
        Counselor newCounselor=counselorService.register(counselor);
        return ResponseMessage.success(newCounselor);
    }

    //登录
    @GetMapping
    public ResponseMessage counselor_login(@RequestParam String name, @RequestParam String password){
        Optional<Counselor> newCounselor=counselorService.login(name,password);
        return ResponseMessage.success(newCounselor);
    }
}
