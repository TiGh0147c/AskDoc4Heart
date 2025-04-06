package com.example.demo.binding.dto;

import com.example.demo.binding.entity.Supervisor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 督导
 */
@Data
@AllArgsConstructor
public class SupervisorDTO {
    private Integer supervisorId;
    private String name;
    private String phoneNumber;
    private String statusMessage;  // 状态消息字段

    public SupervisorDTO(Supervisor supervisor) {
        this.supervisorId = supervisor.getSupervisorId();
        this.name = supervisor.getName();
        this.phoneNumber = supervisor.getPhoneNumber();
        this.statusMessage = "Supervisor found";
    }

    public SupervisorDTO() {
        this.statusMessage = "Supervisor not found";  // 默认状态消息
    }
}
