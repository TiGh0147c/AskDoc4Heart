package com.example.demo.Evaluation.Controller;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Entity.UserEvaluation;
import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationResultDTO;
import com.example.demo.Evaluation.Entity.dto.UserEvaluationDTO;
import com.example.demo.Evaluation.Service.EvaluationService;
import com.example.demo.Evaluation.Service.IEvaluationService;
import com.example.demo.RegisterAndLogin.Entity.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    @Autowired
    private IEvaluationService evaluationService;

    @PostMapping("/user")
    public ResponseMessage<UserEvaluation> userEvaluation(@RequestBody UserEvaluationDTO userEvaluation) {
        UserEvaluation evaluation=evaluationService.user(userEvaluation);
        return ResponseMessage.success(evaluation);
    }

    @GetMapping("/average")
    public ResponseMessage<Average> getAverage(@RequestParam int counselorId) {
        Average average = evaluationService.getAverage(counselorId);
        return ResponseMessage.success(average);
    }

    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<CounselorEvaluationResultDTO> getEvaluationsByCounselorId(@PathVariable int counselorId) {
        CounselorEvaluationResultDTO result = evaluationService.getEvaluationsByCounselorId(counselorId);
        return ResponseEntity.ok(result);
    }
}
