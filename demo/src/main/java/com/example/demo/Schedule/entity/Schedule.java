package com.example.demo.Schedule.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class Schedule {
    @Setter
    @Getter
    private Integer scheduleId;

    @Setter
    @Getter
    private LocalDate date;

    @Setter
    @Getter
    private String timeSlot; // "morning" or "afternoon"
    @Setter
    @Getter
    private Integer counselorId;
    @Setter
    @Getter
    private Integer supervisorId;
    @Setter
    @Getter
    private String supervisorName;
    @Setter
    @Getter
    private String counselorName;
    @Setter
    @Getter
    private String status; // "working" or "on_leave"
}