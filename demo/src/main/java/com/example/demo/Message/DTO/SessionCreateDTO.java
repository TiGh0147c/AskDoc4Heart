package com.example.demo.Message.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SessionCreateDTO {
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotNull(message = "咨询师ID不能为空")
    private Integer counselorId;

    @NotNull(message = "督导ID不能为空")
    private Integer supervisorId;
}
