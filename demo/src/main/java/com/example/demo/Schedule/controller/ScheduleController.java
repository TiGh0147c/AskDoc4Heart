package com.example.demo.Schedule.controller;


import com.example.demo.Schedule.dto.ScheduleDTO;
import com.example.demo.Schedule.entity.Schedule;
import com.example.demo.Schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    // 创建排班
    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@RequestBody Schedule schedule) {
        int result = scheduleService.addSchedule(schedule);
        if (result > 0) {
            return ResponseEntity.ok().body("排班创建成功");
        } else {
            return ResponseEntity.badRequest().body("排班创建失败");
        }
    }

    // 获取所有排班
    @GetMapping("/all")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    // 根据ID获取排班
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        if (schedule != null) {
            return ResponseEntity.ok(schedule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 根据日期获取排班
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Schedule>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDate(date));
    }

    // 更新排班
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Integer id, @RequestBody Schedule schedule) {
        schedule.setScheduleId(id);
        int result = scheduleService.updateSchedule(schedule);
        if (result > 0) {
            return ResponseEntity.ok().body("排班更新成功");
        } else {
            return ResponseEntity.badRequest().body("排班更新失败");
        }
    }

    // 删除排班
    @DeleteMapping("/clear/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        int result = scheduleService.deleteSchedule(id);
        if (result > 0) {
            return ResponseEntity.ok().body("排班删除成功");
        } else {
            return ResponseEntity.badRequest().body("排班删除失败");
        }
    }

    // 获取咨询师的排班
    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<List<Schedule>> getSchedulesByCounselorId(
            @PathVariable Integer counselorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByCounselorId(counselorId));
    }

    // 获取督导安排的排班
    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<Schedule>> getSchedulesBySupervisorId(
            @PathVariable Integer supervisorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesBySupervisorId(supervisorId));
    }
}
