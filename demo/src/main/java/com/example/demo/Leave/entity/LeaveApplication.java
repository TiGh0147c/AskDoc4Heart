package com.example.demo.Leave.entity;


import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class LeaveApplication {
    private Integer leaveId;
    private String leaveReason;
    private String leaveStatus; // "pending", "approved", "rejected"
    private Date applicationDate;
    private String applicationTime; // "morning", "afternoon"
    private String role; // "counselor", "supervisor"
    private Integer applicantId;
    private Integer scheduleId;
}