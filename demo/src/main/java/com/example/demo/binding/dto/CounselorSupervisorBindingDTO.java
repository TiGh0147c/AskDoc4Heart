package com.example.demo.binding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 咨询师和督导的绑定关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CounselorSupervisorBindingDTO {
    private Integer counselorId;
    private String counselorName;
    private String counselorPhoneNumber;
    private Integer supervisorId;
    private String supervisorName;
    private String supervisorPhoneNumber;
}
