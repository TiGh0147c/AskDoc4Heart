package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.RegisterAndLogin.Entity.Administrator;
import com.example.demo.RegisterAndLogin.Entity.dto.AdministratorDTO;
import com.example.demo.RegisterAndLogin.Exception.ServiceException;
import com.example.demo.RegisterAndLogin.Mapper.AdminRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService implements IAdminService {

    @Autowired
    AdminRepository adminRepository;

    @Override
    public Administrator register(AdministratorDTO administrator){
        Administrator admin = new Administrator();
        BeanUtils.copyProperties(administrator,admin);
        Optional<Administrator> exist= adminRepository.findByUsername(admin.getUsername());
        if(exist.isPresent()){
            throw new ServiceException("用户名已存在！");
        }
        return adminRepository.save(admin);
    }

    @Override
    public Optional<Administrator> login(String username, String password){
        Optional<Administrator> administrator= adminRepository.findByUsername(username);
        if(administrator.isPresent()){
            if(administrator.get().getPassword().equals(password)){
                return administrator;
            }
            else{
                throw new ServiceException("用户名或密码错误！");
            }
        }
        throw new ServiceException("用户不存在！");
    }
}
