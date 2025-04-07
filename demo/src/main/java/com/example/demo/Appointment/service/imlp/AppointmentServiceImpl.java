package com.example.demo.Appointment.service.imlp;

import com.example.demo.Appointment.dto.AppointmentDTO;
import com.example.demo.Appointment.entity.Appointment;
import com.example.demo.Appointment.mapper.AppointmentMapper;
import com.example.demo.Appointment.mapper.CounselorScheduleMapper;
import com.example.demo.Appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约服务实现类
 * Appointment Service Implementation
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    // 注入预约数据访问层接口
    // Inject Appointment Mapper interface
    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 创建预约
     * Create an appointment
     * @param appointmentDTO 预约数据传输对象
     * @return 创建后的预约DTO/null(创建失败时)
     */


    @Override
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        // 检查预约日期是否在未来7天内
        LocalDate appointmentDate = LocalDate.parse(appointmentDTO.getAppointmentDate());
        if (appointmentDate.isAfter(LocalDate.now().plusDays(7)) || appointmentDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("预约仅限于未来7天以内");
        }
        Integer userId = appointmentDTO.getUserId();

        // 检查当前用户是否已经有超过两个waiting状态的预约
        // 检查waiting状态预约数量（阈值改为2）
        if (isUserOverAppointmentLimit(appointmentDTO.getUserId()) >= 2) {
            throw new RuntimeException("用户已达到最多2个等待中的预约限制");
        }

        // 检查咨询师在指定日期和时间段是否有排班
        if (!isCounselorScheduledForAppointment(appointmentDTO.getCounselorId(), appointmentDate, appointmentDTO.getAppointmentTime())) {
            throw new RuntimeException("所选咨询师不在该日期和时间段的值班表中");
        }
        //检查当前咨询师在相同时间段内是否已经超过最大预约数10
        if (isCounselorOverAppointmentLimit(appointmentDTO.getCounselorId(), LocalDate.parse(appointmentDTO.getAppointmentDate()), appointmentDTO.getAppointmentTime())) {
            throw new RuntimeException("Counselor has reached the maximum number of appointments for this time slot.");
        }

        // 将DTO转换为实体对象
        Appointment appointment = convertToEntity(appointmentDTO);

        // 调用Mapper插入数据到数据库
        int result = appointmentMapper.insertAppointment(appointment);

        // 如果插入成功(影响行数>0)，返回转换后的DTO
        if (result > 0) {
            return convertToDTO(appointment, appointmentDTO.getUserName(), appointmentDTO.getCounselorName());
        }
        return null; // 创建失败返回null
    }
    /**
     * 根据用户ID获取预约列表
     * Get appointments by user ID
     * @param userId 用户ID
     * @return 预约DTO列表
     */
    @Override
    public List<AppointmentDTO> getAppointmentsByUserId(Integer userId) {
        // 通过Mapper从数据库获取预约实体列表
        // Get appointment entities from database via Mapper
        List<Appointment> appointments = appointmentMapper.getAppointmentsByUserId(userId);

        // 使用Stream将实体列表转换为DTO列表
        // Convert entity list to DTO list using Stream
        return appointments.stream()
                // 暂时传入空字符串作为用户名和咨询师名(实际项目应从用户服务获取)
                // Temporarily pass empty strings for names (in real project should get from user service)
                .map(appointment -> convertToDTO(appointment, "", ""))
                .collect(Collectors.toList());
    }

    /**
     * 根据咨询师ID获取预约列表
     * Get appointments by counselor ID
     * @param counselorId 咨询师ID
     * @return 预约DTO列表
     */
    @Override
    public List<AppointmentDTO> getAppointmentsByCounselorId(Integer counselorId) {
        List<Appointment> appointments = appointmentMapper.getAppointmentsByCounselorId(counselorId);
        return appointments.stream()
                .map(appointment -> convertToDTO(appointment, "", ""))
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID获取未完成的预约
     * @param userId
     * @return
     */
    @Override
    public List<AppointmentDTO> getUncompletedAppointmentsByUserId(Integer userId) {
        List<Appointment> appointments = appointmentMapper.getUncompletedAppointmentsByUserId(userId);
        return appointments.stream()
                .map(appointment -> convertToDTO(appointment, "", ""))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有预约列表
     * Get all appointments
     * @return 所有预约DTO列表
     */
    @Override
    public List<AppointmentDTO> getAllAppointments() {
        List<Appointment> appointments = appointmentMapper.getAllAppointments();
        return appointments.stream()
                .map(appointment -> convertToDTO(appointment, "", ""))
                .collect(Collectors.toList());
    }

    /**
     * 取消预约
     * Cancel an appointment
     * @param appointmentId 预约ID
     * @return true-取消成功/false-取消失败
     */
    @Override
    public boolean cancelAppointment(Integer appointmentId) {
        // 更新状态为"cancelled"
        // Update status to "cancelled"
        int result = appointmentMapper.updateAppointmentStatus(appointmentId, "cancelled");
        // 返回是否更新成功
        // Return whether update was successful
        return result > 0;
    }

    /**
     * 更新预约状态
     * Update appointment status
     * @param appointmentId 预约ID
     * @param newStatus 新状态
     * @return true-更新成功/false-更新失败
     */
    @Override
    public boolean updateAppointmentStatus(Integer appointmentId, String newStatus) {
        int result = appointmentMapper.updateAppointmentStatus(appointmentId, newStatus);
        return result > 0;
    }



    /**
     * 将DTO转换为实体对象
     * Convert DTO to Entity
     * @param dto 预约数据传输对象
     * @return 预约实体对象
     */
    private Appointment convertToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(dto.getAppointmentId());
        // 正确解析日期
        appointment.setAppointmentDate(LocalDate.parse(dto.getAppointmentDate()));
        // 直接传递时段字符串（需确保值为英文）
        appointment.setAppointmentTime(
                "下午".equals(dto.getAppointmentTime()) ? "afternoon" : "morning"
        );
        appointment.setAppointmentStatus(dto.getAppointmentStatus());
        appointment.setUserId(dto.getUserId());
        appointment.setCounselorId(dto.getCounselorId());
        return appointment;
    }

    /**
     * 将实体对象转换为DTO
     * Convert Entity to DTO
     * @param appointment 预约实体对象
     * @param userName 用户名
     * @param counselorName 咨询师名
     * @return 预约数据传输对象
     */
    private AppointmentDTO convertToDTO(Appointment appointment, String userName, String counselorName) {
        return new AppointmentDTO(appointment, userName, counselorName);
    }
    private int isUserOverAppointmentLimit(Integer userId) {
        // 只查询 scheduled 状态的预约
        List<Appointment> waitingAppointments = appointmentMapper.getAppointmentsByUserIdAndStatus(
                userId,
                "scheduled"  // 或你的实际状态值
        );
        return waitingAppointments.size();
    }



    @Autowired
    private CounselorScheduleMapper counselorScheduleMapper;

    private boolean isCounselorScheduledForAppointment(Integer counselorId, LocalDate date, String timeSlot) {
        // 转换时间槽为小写以匹配数据库存储格式
        timeSlot = timeSlot.toLowerCase();

        // 查询是否有对应的排班记录
        return counselorScheduleMapper.existsByCounselorAndDateAndTimeSlot(Long.valueOf(counselorId), date, timeSlot);
    }

    private boolean isCounselorOverAppointmentLimit(Integer counselorId, LocalDate appointmentDate, String appointmentTime) {
        List<Appointment> counselorAppointments = appointmentMapper.getAppointmentsByCounselorIdAndTime(counselorId, appointmentDate, appointmentTime,"waiting");
        return counselorAppointments.size() >= 10;
    }
}