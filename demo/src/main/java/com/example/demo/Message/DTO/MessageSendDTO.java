package com.example.demo.Message.DTO;
import com.example.demo.Message.Entity.SessionMessage;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MessageSendDTO {
    @NotNull(message = "发送者角色不能为空")
    private SessionMessage.SenderRole senderRole;

    @NotNull(message = "发送者ID不能为空")
    private Integer senderId;

    @NotNull(message = "消息类型不能为空")
    private SessionMessage.MessageType messageType;

    @NotBlank(message = "消息内容不能为空")
    private String messageContent;
}