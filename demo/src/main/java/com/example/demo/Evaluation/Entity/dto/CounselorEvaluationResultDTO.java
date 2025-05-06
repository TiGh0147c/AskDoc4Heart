package com.example.demo.Evaluation.Entity.dto;

import com.example.demo.Evaluation.Entity.UserEvaluation;

import java.util.List;

public class CounselorEvaluationResultDTO {
    private List<UserEvaluation> evaluations;
    private double averageRating;
    private int evaluationCount;

    public List<UserEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<UserEvaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(int evaluationCount) {
        this.evaluationCount = evaluationCount;
    }
}