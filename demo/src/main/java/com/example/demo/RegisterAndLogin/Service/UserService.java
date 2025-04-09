package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.RegisterAndLogin.Entity.User;
import com.example.demo.RegisterAndLogin.Entity.dto.UserDTO;
import com.example.demo.RegisterAndLogin.Exception.ServiceException;
import com.example.demo.RegisterAndLogin.Mapper.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User register(UserDTO user){
        User newUser = new User();
        BeanUtils.copyProperties(user,newUser);  //userDTO类转为user类
        Optional<User> exist= userRepository.findByUsername(newUser.getUsername());
        if(exist.isPresent()){
            throw new ServiceException("用户名已存在！");
        }
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> login(String username, String password){
        Optional<User> user= userRepository.findByUsername(username);
        if(user.isPresent()){
            if(user.get().getPassword().equals(password)){
                return user;
            }
            else{
                throw new ServiceException("用户名或密码错误！");
            }
        }
        throw new ServiceException("用户不存在！");
    }

    @Override
    public Optional<User> login(String openId){
        Optional<User> user= userRepository.findByOpenId(openId);
        if(user.isPresent()){
            return user;
        }
        throw new ServiceException("用户不存在！");
    }
}
