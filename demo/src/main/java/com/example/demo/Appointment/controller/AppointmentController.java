package com.example.demo.Appointment.controller;

import java.util.Collections;
import com.example.demo.Appointment.dto.AppointmentDTO;
import com.example.demo.Appointment.service.AppointmentService; // 注入接口而不是实现类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService; // 注入接口

    // 创建预约
    @PostMapping("/create")
    public ResponseEntity<String> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO createdAppointment = appointmentService.createAppointment(appointmentDTO);
        if (createdAppointment != null) {
            return ResponseEntity.ok("Appointment created successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create appointment!");
        }
    }

    // 根据用户ID获取所有预约
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByUserId(@PathVariable Integer userId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByUserId(userId);
        if (appointments != null && !appointments.isEmpty()) {
            return ResponseEntity.ok(appointments);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    // 根据咨询师ID获取所有预约
    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByCounselorId(@PathVariable Integer counselorId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByCounselorId(counselorId);
        if (appointments != null && !appointments.isEmpty()) {
            return ResponseEntity.ok(appointments);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    // 获取所有预约
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        if (appointments != null && !appointments.isEmpty()) {
            return ResponseEntity.ok(appointments);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    // 取消预约
    @PostMapping("/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Integer appointmentId) {
        boolean isCancelled = appointmentService.cancelAppointment(appointmentId);
        if (isCancelled) {
            return ResponseEntity.ok("Appointment cancelled successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel appointment!");
        }
    }

    // 更新预约状态（例如更改为已完成）
    @PostMapping("/update/{appointmentId}")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable Integer appointmentId,
            @RequestBody Map<String, String> request) {
        String newStatus = request.get("newStatus");
        boolean isUpdated = appointmentService.updateAppointmentStatus(appointmentId, newStatus);
        if (isUpdated) {
            return ResponseEntity.ok("Appointment status updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update appointment status!");
        }
    }



}
