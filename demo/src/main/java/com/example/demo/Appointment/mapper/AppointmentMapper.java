package com.example.demo.Appointment.mapper;


import com.example.demo.Appointment.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 预约记录 Mapper
 */
@Mapper
public interface AppointmentMapper {

    // 插入预约记录
    int insertAppointment(Appointment appointment);

    // 根据用户ID查询所有预约记录
    List<Appointment> getAppointmentsByUserId(Integer userId);

    // 根据咨询师ID查询所有预约记录
    List<Appointment> getAppointmentsByCounselorId(Integer counselorId);

    // 根据预约ID查询预约记录
    Appointment getAppointmentById(Integer appointmentId);

    // 更新预约状态
   // int updateAppointmentStatus(Integer appointmentId, String status);

    int updateAppointmentStatus(@Param("appointmentId") Integer appointmentId, @Param("newStatus") String newStatus);
    // 删除预约记录
    int deleteAppointment(Integer appointmentId);

    // 获取所有预约记录
    List<Appointment> getAllAppointments();

    // 根据预约日期查询预约记录
    List<Appointment> getAppointmentsByDate(String appointmentDate);
    //根据咨询师ID、预约日期和时间获取预约列表
    List<Appointment> getAppointmentsByCounselorIdAndTime(@Param("counselorId") Integer counselorId,
                                                          @Param("appointmentDate") LocalDate appointmentDate,
                                                          @Param("appointmentTime") String appointmentTime,
                                                          @Param("appointmentStatus")String appointmentStatus);
    List<Appointment> getUncompletedAppointmentsByUserId(@Param("userId") Integer userId);


    List<Appointment> getAppointmentsByUserIdAndStatus(
            @Param("userId") Integer userId,
            @Param("appointmentStatus") String status
    );
}
