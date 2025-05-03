package com.example.demo.counseling.controller;

import com.example.demo.counseling.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class WebsocketAuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/token")
    public ResponseEntity<String> generateToken(
            @RequestParam String username,
            @RequestParam String role) {
        String token = tokenProvider.createToken(username, role);
        return ResponseEntity.ok(token);
    }
}