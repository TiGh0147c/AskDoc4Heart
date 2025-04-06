package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.RegisterAndLogin.Entity.User;
import com.example.demo.RegisterAndLogin.Entity.dto.UserDTO;

import java.util.Optional;

public interface IUserService {
    User register(UserDTO user);
    Optional<User> login(String username, String password);
}
