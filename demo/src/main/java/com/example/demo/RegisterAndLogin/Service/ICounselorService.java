package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.dto.CounselorDTO;

import java.util.Optional;

public interface ICounselorService {
    Counselor register(CounselorDTO counselor);
    Optional<Counselor> login(String name, String password);
}
