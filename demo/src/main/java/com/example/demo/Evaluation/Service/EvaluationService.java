package com.example.demo.Evaluation.Service;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Entity.CounselorEvaluation;
import com.example.demo.Evaluation.Entity.Session;
import com.example.demo.Evaluation.Entity.UserEvaluation;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationDTO;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationResultDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        return userEvaluationRepository.save(evaluation);
    }

    @Override
    public Counselor getCounselor (int counselorId){
        return counselorRepository.findById(counselorId).get();
    }

    @Override
    public Average getAverage (int counselorId){
        return averageRepository.findAverageByCounselorId(counselorId);
    }
    
    @Override
    public CounselorEvaluationResultDTO getEvaluationsByCounselorId(int counselorId) {
        // 1. 查找该咨询师的所有会话
        List<Session> sessions = StreamSupport.stream(sessionRepository.findAll().spliterator(), false)
                .filter(session -> session.getCounselorId() == counselorId)
                .collect(Collectors.toList());
        
        // 2. 获取所有会话ID
        List<Integer> sessionIds = sessions.stream()
                .map(Session::getSessionId)
                .collect(Collectors.toList());
        
        // 3. 根据会话ID列表查询所有用户评价
        List<UserEvaluation> evaluations = new ArrayList<>();
        if (!sessionIds.isEmpty()) {
            evaluations = userEvaluationRepository.findBySessionIdIn(sessionIds);
        }
        
        // 4. 获取平均分信息
        Average average = averageRepository.findAverageByCounselorId(counselorId);
        
        // 5. 组装结果
        CounselorEvaluationResultDTO result = new CounselorEvaluationResultDTO();
        result.setEvaluations(evaluations);
        
        if (average != null) {
            result.setAverageRating(average.getAverage());
            result.setEvaluationCount(average.getCount());
        } else {
            // 如果没有平均分记录，则计算当前查询到的评价的平均分
            if (!evaluations.isEmpty()) {
                double avgRating = evaluations.stream()
                        .mapToInt(UserEvaluation::getRating)
                        .average()
                        .orElse(0.0);
                result.setAverageRating(avgRating);
                result.setEvaluationCount(evaluations.size());
            } else {
                result.setAverageRating(0.0);
                result.setEvaluationCount(0);
            }
        }
        
        return result;
    }
}
