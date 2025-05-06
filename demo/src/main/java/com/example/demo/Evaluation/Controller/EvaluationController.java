package com.example.demo.Evaluation.Controller;

import com.example.demo.Evaluation.Entity.dto.CounselorEvaluationResultDTO;
import com.example.demo.Evaluation.Service.IEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    @Autowired
    private IEvaluationService evaluationService;

    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<CounselorEvaluationResultDTO> getEvaluationsByCounselorId(@PathVariable int counselorId) {
        CounselorEvaluationResultDTO result = evaluationService.getEvaluationsByCounselorId(counselorId);
        return ResponseEntity.ok(result);
    }
}
