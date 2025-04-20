package com.example.demo.attendance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    private Integer scheduleId;
    private LocalDate date;
    private String timeSlot; // 'morning' or 'afternoon'

    private Integer counselorId;
    private Integer supervisorId;
}
