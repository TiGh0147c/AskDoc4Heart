package com.example.demo.Evaluation.Controller;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Entity.CounselorEvaluation;
import com.example.demo.Evaluation.Entity.UserEvaluation;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationDTO;
import com.example.demo.Evaluation.Entity.dto.UserEvaluationDTO;
import com.example.demo.Evaluation.Service.EvaluationService;
import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/counselor")
    public ResponseMessage<CounselorEvaluation> counselorEvaluation(@RequestBody CounselorEvaluationDTO counselorEvaluation) {
        CounselorEvaluation evaluation = evaluationService.counselor(counselorEvaluation);
        return ResponseMessage.success(evaluation);
    }

    @PostMapping("/user")
    public ResponseMessage<UserEvaluation> userEvaluation(@RequestBody UserEvaluationDTO userEvaluation) {
        UserEvaluation evaluation=evaluationService.user(userEvaluation);
        return ResponseMessage.success(evaluation);
    }

    @GetMapping("/counselor")
    public ResponseMessage<Counselor> getCounselor(@RequestParam int counselorId) {
        Counselor counselor=evaluationService.getCounselor(counselorId);
        return ResponseMessage.success(counselor);
    }

    @GetMapping("/average")
    public ResponseMessage<Average> getAverage(@RequestParam int counselorId) {
        Average average=evaluationService.getAverage(counselorId);
        return ResponseMessage.success(average);
    }
}
