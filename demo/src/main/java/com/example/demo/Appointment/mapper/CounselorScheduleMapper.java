package com.example.demo.Appointment.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
@Mapper
public interface CounselorScheduleMapper {
    /**
     * 检查咨询师在特定日期和时间段是否有排班
     * @param counselorId 咨询师ID
     * @param appointmentDate 预约日期
     * @param timeSlot 时间段（"morning" 或 "afternoon"）
     * @return 存在记录则返回true，否则返回false
     */

    boolean existsByCounselorAndDateAndTimeSlot(@Param("counselorId") Long counselorId,
                                                @Param("appointmentDate") LocalDate appointmentDate,
                                                @Param("timeSlot") String timeSlot);
}