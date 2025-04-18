package com.example.demo.Evaluation.Entity.dto;

public class CounselorEvaluationDTO {
    private int evaluationId;
    private String psychological_state_rating;
    private String communication_ability_rating;
    private String emotional_stability_rating;
    private String user_visible_part;
    private String user_invisible_part;
    private String evaluation_time;
    private int session_id;

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getPsychological_state_rating() {
        return psychological_state_rating;
    }

    public void setPsychological_state_rating(String psychological_state_rating) {
        this.psychological_state_rating = psychological_state_rating;
    }

    public String getCommunication_ability_rating() {
        return communication_ability_rating;
    }

    public void setCommunication_ability_rating(String communication_ability_rating) {
        this.communication_ability_rating = communication_ability_rating;
    }

    public String getEmotional_stability_rating() {
        return emotional_stability_rating;
    }

    public void setEmotional_stability_rating(String emotional_stability_rating) {
        this.emotional_stability_rating = emotional_stability_rating;
    }

    public String getUser_visible_part() {
        return user_visible_part;
    }

    public void setUser_visible_part(String user_visible_part) {
        this.user_visible_part = user_visible_part;
    }

    public String getUser_invisible_part() {
        return user_invisible_part;
    }

    public void setUser_invisible_part(String user_invisible_part) {
        this.user_invisible_part = user_invisible_part;
    }

    public String getEvaluation_time() {
        return evaluation_time;
    }

    public void setEvaluation_time(String evaluation_time) {
        this.evaluation_time = evaluation_time;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    @Override
    public String toString() {
        return "CounselorEvaluationDTO{" +
                "evaluationId=" + evaluationId +
                ", psychological_state_rating='" + psychological_state_rating + '\'' +
                ", communication_ability_rating='" + communication_ability_rating + '\'' +
                ", emotional_stability_rating='" + emotional_stability_rating + '\'' +
                ", user_visible_part='" + user_visible_part + '\'' +
                ", user_invisible_part='" + user_invisible_part + '\'' +
                ", evaluation_time='" + evaluation_time + '\'' +
                ", session_id=" + session_id +
                '}';
    }
}
