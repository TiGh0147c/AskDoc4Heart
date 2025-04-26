package com.example.demo.Schedule.controller;

import com.example.demo.Schedule.entity.Schedule;
import com.example.demo.Schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@RequestBody Schedule schedule) {
        int result = scheduleService.addSchedule(schedule);
        if (result > 0) {
            return ResponseEntity.ok().body("排班创建成功");
        } else {
            return ResponseEntity.badRequest().body("排班创建失败");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        if (schedule != null) {
            return ResponseEntity.ok(schedule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Schedule>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDate(date));
    }

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

    @DeleteMapping("/clear/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        int result = scheduleService.deleteSchedule(id);
        if (result > 0) {
            return ResponseEntity.ok().body("排班删除成功");
        } else {
            return ResponseEntity.badRequest().body("排班删除失败");
        }
    }

    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<List<Schedule>> getSchedulesByCounselorId(
            @PathVariable Integer counselorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByCounselorId(counselorId));
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<Schedule>> getSchedulesBySupervisorId(
            @PathVariable Integer supervisorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesBySupervisorId(supervisorId));
    }
}