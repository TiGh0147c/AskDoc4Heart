package com.example.demo.Appointment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
@Mapper
public interface SupervisorScheduleMapper {
    /**
     * 检查督导在特定日期和时间段是否有排班
     * @param supervisorId 督导ID
     * @param date 预约日期
     * @param timeSlot 时间段（"morning" 或 "afternoon"）
     * @return 存在记录则返回true，否则返回false
     */
    boolean existsBySupervisorAndDateAndTimeSlot(@Param("supervisorId") Integer supervisorId,
                                                 @Param("date") LocalDate date,
                                                 @Param("timeSlot") String timeSlot);
}