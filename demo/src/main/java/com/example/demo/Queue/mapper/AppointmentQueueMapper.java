package com.example.demo.Queue.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

@Mapper
public interface AppointmentQueueMapper {
    int countValidAppointments(
            @Param("counselorId") Integer counselorId,
            @Param("date") LocalDate date,
            @Param("status") String status);
}