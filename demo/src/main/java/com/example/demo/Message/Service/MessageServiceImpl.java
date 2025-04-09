package com.example.demo.Message.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.Message.DTO.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CounselorRepository counselorRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Integer sendMessage(Integer sessionId, MessageSendDTO sendDTO) {
        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("会话不存在"));

        // 验证发送者权限
        validateSenderPermission(session, sendDTO.getSenderRole(), sendDTO.getSenderId());

        SessionMessage message = new SessionMessage();
        message.setSessionId(sessionId);
        message.setSenderRole(sendDTO.getSenderRole());
        message.setSenderId(sendDTO.getSenderId());
        message.setMessageType(sendDTO.getMessageType());
        message.setMessageContent(sendDTO.getMessageContent());

        SessionMessage savedMessage = messageRepository.save(message);

        // 更新会话最后消息时间
        session.setLastMessageSentTime(LocalDateTime.now());
        sessionRepository.save(session);

        return savedMessage.getMessageId();
    }

    @Override
    public PageResult<MessageVO> getMessageHistory(Integer sessionId, PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(
                pageQuery.getPage() - 1,
                pageQuery.getSize(),
                Sort.by(Sort.Direction.DESC, "messageSentTime")
        );

        Page<SessionMessage> page = messageRepository.findBySessionId(sessionId, pageable);

        List<MessageVO> messages = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotalElements(), messages);
    }

    @Override
    public FileUploadResult uploadFile(Integer sessionId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 验证文件类型和大小
        validateFile(file);

        try {
            String fileName = fileStorageService.storeFile(file);
            String fileUrl = "/uploads/" + fileName;

            return new FileUploadResult(
                    fileName,
                    fileUrl,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public MessageVO getMessageById(Integer messageId) {
        SessionMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("消息不存在"));
        return convertToVO(message);
    }

    private MessageVO convertToVO(SessionMessage message) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(message.getMessageId());
        vo.setSessionId(message.getSessionId());
        vo.setSenderRole(message.getSenderRole().name());
        vo.setSenderId(message.getSenderId());
        vo.setMessageType(message.getMessageType().name());
        vo.setMessageContent(message.getMessageContent());
        vo.setSentTime(message.getMessageSentTime().toString());

        // 设置发送者信息
        if (message.getSenderRole() == SessionMessage.SenderRole.USER) {
            User user = userRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
            vo.setSenderName(user.getNickname());
            vo.setSenderAvatar(user.getAvatar());
        } else {
            Counselor counselor = counselorRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new ResourceNotFoundException("咨询师不存在"));
            vo.setSenderName(counselor.getName());
            vo.setSenderAvatar(counselor.getAvatar());
        }

        return vo;
    }

    private void validateSenderPermission(ConsultationSession session,
                                          SessionMessage.SenderRole senderRole, Integer senderId) {

        if (senderRole == SessionMessage.SenderRole.USER &&
                !session.getUserId().equals(senderId)) {
            throw new BusinessException("用户无权在此会话发送消息");
        }

        if (senderRole == SessionMessage.SenderRole.COUNSELOR &&
                !session.getCounselorId().equals(senderId)) {
            throw new BusinessException("咨询师无权在此会话发送消息");
        }
    }

    private void validateFile(MultipartFile file) {
        // 允许的文件类型
        List<String> allowedTypes = List.of(
                "image/jpeg",
                "image/png",
                "application/pdf",
                "text/plain"
        );

        // 最大文件大小 5MB
        long maxSize = 5 * 1024 * 1024;

        if (!allowedTypes.contains(file.getContentType())) {
            throw new BusinessException("不支持的文件类型");
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过5MB");
        }
    }
}