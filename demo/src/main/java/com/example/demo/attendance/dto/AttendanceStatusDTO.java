package com.example.demo.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceStatusDTO {
    private boolean shouldCheckIn;  // 是否显示“打卡上班”
    private boolean shouldCheckOut; // 是否显示“打卡下班”
}
