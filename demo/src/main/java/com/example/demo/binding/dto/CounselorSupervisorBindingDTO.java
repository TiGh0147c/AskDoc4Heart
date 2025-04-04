package com.example.demo.binding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
