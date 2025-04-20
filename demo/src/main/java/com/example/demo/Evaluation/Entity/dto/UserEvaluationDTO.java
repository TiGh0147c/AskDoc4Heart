package com.example.demo.Evaluation.Entity.dto;

public class UserEvaluationDTO {
    private int evaluationId;
    private String evaluation_content;
    private int rating;
    private int session_id;
    private String evaluation_time;

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getEvaluation_content() {
        return evaluation_content;
    }

    public void setEvaluation_content(String evaluation_content) {
        this.evaluation_content = evaluation_content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public String getEvaluation_time() {
        return evaluation_time;
    }

    public void setEvaluation_time(String evaluation_time) {
        this.evaluation_time = evaluation_time;
    }

    @Override
    public String toString() {
        return "UserEvaluation{" +
                "evaluationId=" + evaluationId +
                ", evaluation_content='" + evaluation_content + '\'' +
                ", rating=" + rating +
                ", session_id=" + session_id +
                ", evaluation_time='" + evaluation_time + '\'' +
                '}';
    }
}
