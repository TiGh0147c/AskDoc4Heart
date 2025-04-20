package com.example.demo.Evaluation.Service;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Entity.CounselorEvaluation;
import com.example.demo.Evaluation.Entity.UserEvaluation;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationDTO;
import com.example.demo.Evaluation.Entity.dto.UserEvaluationDTO;
import com.example.demo.Evaluation.Mapper.AverageRepository;
import com.example.demo.Evaluation.Mapper.CounselorEvaluationRepository;
import com.example.demo.Evaluation.Mapper.SessionRepository;
import com.example.demo.Evaluation.Mapper.UserEvaluationRepository;
import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.User;
import com.example.demo.RegisterAndLogin.Exception.ServiceException;
import com.example.demo.RegisterAndLogin.Mapper.CounselorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EvaluationService implements IEvaluationService {

    @Autowired
    CounselorEvaluationRepository counselorEvaluationRepository;

    @Autowired
    UserEvaluationRepository userEvaluationRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    AverageRepository averageRepository;

    @Autowired
    CounselorRepository counselorRepository;

    @Override
    public CounselorEvaluation counselor (CounselorEvaluationDTO counselorEvaluation){
        CounselorEvaluation evaluation = new CounselorEvaluation();
        BeanUtils.copyProperties(counselorEvaluation,evaluation);
        return counselorEvaluationRepository.save(evaluation);
    }

    @Override
    public UserEvaluation user (UserEvaluationDTO userEvaluation){
        UserEvaluation evaluation = new UserEvaluation();
        BeanUtils.copyProperties(userEvaluation,evaluation);
        userEvaluationRepository.save(evaluation);

        //修改平均分
        int sessionId=evaluation.getSession_id();
        int counselorId=sessionRepository.findById(sessionId).get().getCounselorId();
        Average average=averageRepository.findAverageByCounselorId(counselorId);
        average.setAverage(evaluation.getRating());
        average.setCount();
        averageRepository.save(average);

        return  userEvaluationRepository.save(evaluation);
    }

    @Override
    public Counselor getCounselor (int counselorId){
        return counselorRepository.findById(counselorId).get();
    }

    @Override
    public Average getAverage (int counselorId){
        return averageRepository.findAverageByCounselorId(counselorId);
    }
}
