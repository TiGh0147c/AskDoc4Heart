package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.dto.CounselorDTO;
import com.example.demo.RegisterAndLogin.Exception.ServiceException;
import com.example.demo.RegisterAndLogin.Mapper.CounselorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CounselorService implements ICounselorService {

    @Autowired
    private CounselorRepository counselorRepository;

    @Override
    public Counselor register(CounselorDTO counselor){
        Counselor newCounselor = new Counselor();
        BeanUtils.copyProperties(counselor,newCounselor);
        Optional<Counselor> exist= counselorRepository.findByName(newCounselor.getName());
        if(exist.isPresent()){
            throw new ServiceException("用户名已存在！");
        }
        return counselorRepository.save(newCounselor);
    }

    @Override
    public Optional<Counselor> login(String name, String password){
        Optional<Counselor> counselor= counselorRepository.findByName(name);
        if(counselor.isPresent()){
            if(counselor.get().getPassword().equals(password)){
                return counselor;
            }
            else{
                throw new ServiceException("用户名或密码错误！");
            }
        }
        throw new ServiceException("用户不存在！");
    }
}
