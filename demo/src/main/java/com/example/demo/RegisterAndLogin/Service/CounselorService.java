package com.example.demo.RegisterAndLogin.Service;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Mapper.AverageRepository;
import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.Supervisor;
import com.example.demo.RegisterAndLogin.Entity.dto.CounselorDTO;
import com.example.demo.RegisterAndLogin.Exception.ServiceException;
import com.example.demo.RegisterAndLogin.Mapper.CounselorRepository;
import com.example.demo.RegisterAndLogin.Mapper.SupervisorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CounselorService implements ICounselorService {

    @Autowired
    private CounselorRepository counselorRepository;

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Autowired
    private AverageRepository averageRepository;

    @Override
    public Counselor register(CounselorDTO counselor){
        Counselor newCounselor = new Counselor();
        BeanUtils.copyProperties(counselor,newCounselor);
        Optional<Counselor> exist= counselorRepository.findByName(newCounselor.getName());
        if(exist.isPresent()){
            throw new ServiceException("用户名已存在！");
        }
        if(newCounselor.getIs_supervisor()){
            Supervisor supervisor = new Supervisor();
            BeanUtils.copyProperties(counselor,supervisor);
            supervisorRepository.save(supervisor);
        }

        counselorRepository.save(newCounselor);

        Average average = new Average();
        average.setCounselorId(newCounselor.getCounselor_id());
        averageRepository.save(average);

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
