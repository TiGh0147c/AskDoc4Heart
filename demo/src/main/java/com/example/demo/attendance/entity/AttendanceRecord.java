package com.example.demo.attendance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecord {
    private Integer attendanceId;
    private String role; // 'counselor' or 'supervisor'

    private Integer counselorId;
    private Integer supervisorId;

    private LocalDateTime checkInTime;
    private String status; // 'check_in' or 'check_out'
    private String attendanceStatus; // 'on_time', 'late'
}
