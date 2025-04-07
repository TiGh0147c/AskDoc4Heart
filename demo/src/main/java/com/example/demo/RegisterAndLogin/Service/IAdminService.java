package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.RegisterAndLogin.Entity.Administrator;
import com.example.demo.RegisterAndLogin.Entity.dto.AdministratorDTO;

import java.util.Optional;

public interface IAdminService {
    Administrator register(AdministratorDTO administrator);
    Optional<Administrator> login(String username, String password);
}
