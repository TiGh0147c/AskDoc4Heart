package com.example.demo.Message.DTO;

import lombok.Data;

@Data
public class SessionDetailVO {
    private Integer sessionId;
    private String sessionStartTime;
    private String sessionEndTime;
    private String status;
    private CounselorInfo counselor;
    private UserInfo user;
    private String lastMessage;
    private String lastMessageTime;
    private Boolean userHasEvaluated;
    private Boolean counselorHasEvaluated;
    private String summary;

    @Data
    public static class CounselorInfo {
        private Integer counselorId;
        private String name;
        private String avatar;
    }

    @Data
    public static class UserInfo {
        private Integer userId;
        private String nickname;
        private String avatar;
    }
}