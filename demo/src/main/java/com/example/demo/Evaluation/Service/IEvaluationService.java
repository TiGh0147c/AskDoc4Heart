package com.example.demo.Evaluation.Service;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Entity.CounselorEvaluation;
import com.example.demo.Evaluation.Entity.UserEvaluation;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationDTO;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationResultDTO;
import com.example.demo.Evaluation.Entity.dto.UserEvaluationDTO;
import com.example.demo.RegisterAndLogin.Entity.Counselor;

public interface IEvaluationService {
    CounselorEvaluation counselor (CounselorEvaluationDTO counselorEvaluation);
    UserEvaluation user (UserEvaluationDTO userEvaluation);
    Counselor getCounselor (int counselorId);
    Average getAverage (int counselorId);
    
    // 根据咨询师ID查询所有用户评价及平均分
    CounselorEvaluationResultDTO getEvaluationsByCounselorId(int counselorId);
}
