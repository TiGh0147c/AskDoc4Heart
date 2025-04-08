package com.example.demo.Message.Service;

import com.example.demo.Message.DTO.*;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    /**
     * 发送消息
     */
    Integer sendMessage(Integer sessionId, MessageSendDTO sendDTO);

    /**
     * 获取消息历史
     */
    PageResult<MessageVO> getMessageHistory(Integer sessionId, PageQuery pageQuery);

    /**
     * 上传文件
     */
    FileUploadResult uploadFile(Integer sessionId, MultipartFile file);

    /**
     * 根据ID获取消息（内部使用）
     */
    MessageVO getMessageById(Integer messageId);
}