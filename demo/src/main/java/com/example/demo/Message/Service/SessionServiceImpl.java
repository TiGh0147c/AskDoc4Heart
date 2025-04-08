package com.example.demo.Message.Service;

import com.example.demo.Message.Entity.SessionMessage;
import com.example.demo.Message.Exception.ResourceNotFoundException;
import com.example.demo.Message.Repository.*;
import com.example.demo.RegisterAndLogin.Entity.User;
import com.example.demo.profilemanagement.entity.Counselor;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.Service;
import com.example.demo.Message.DTO.*;
import com.example.demo.Message.Entity.ConsultationSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final CounselorRepository counselorRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public PageResult<SessionVO> getSessionList(SessionQueryDTO queryDTO, PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.getPage() - 1, pageQuery.getSize(),
                Sort.by(Sort.Direction.DESC, "sessionStartTime"));

        Specification<ConsultationSession> spec = Specification.where(null);

        if (queryDTO.getUserId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("userId"), queryDTO.getUserId()));
        }

        if (queryDTO.getCounselorId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("counselorId"), queryDTO.getCounselorId()));
        }

        if (StringUtils.isNotBlank(queryDTO.getStatus())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("sessionStatus"),
                            ConsultationSession.SessionStatus.valueOf(queryDTO.getStatus())));
        }

        Page<ConsultationSession> page = sessionRepository.findAll(spec, pageable);

        List<SessionVO> sessionVOs = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotalElements(), sessionVOs);
    }

    @Override
    public Integer createSession(SessionCreateDTO createDTO) {
        ConsultationSession session = new ConsultationSession();
        session.setUserId(createDTO.getUserId());
        session.setCounselorId(createDTO.getCounselorId());
        session.setSupervisorId(createDTO.getSupervisorId());
        session.setSessionStartTime(LocalDateTime.now());
        session.setSessionStatus(ConsultationSession.SessionStatus.IN_PROGRESS);

        ConsultationSession savedSession = sessionRepository.save(session);
        return savedSession.getSessionId();
    }

    @Override
    public SessionDetailVO getSessionDetail(Integer sessionId) {
        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("会话不存在"));

        Counselor counselor = counselorRepository.findById(session.getCounselorId())
                .orElseThrow(() -> new ResourceNotFoundException("咨询师不存在"));

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        SessionMessage lastMessage = messageRepository.findTopBySessionIdOrderByMessageSentTimeDesc(sessionId)
                .orElse(null);

        SessionDetailVO detailVO = new SessionDetailVO();
        detailVO.setSessionId(session.getSessionId());
        detailVO.setSessionStartTime(session.getSessionStartTime().toString());
        if (session.getSessionEndTime() != null) {
            detailVO.setSessionEndTime(session.getSessionEndTime().toString());
        }
        detailVO.setStatus(session.getSessionStatus().name());

        SessionDetailVO.CounselorInfo counselorInfo = new SessionDetailVO.CounselorInfo();
        counselorInfo.setCounselorId(counselor.getCounselorId());
        counselorInfo.setName(counselor.getName());
        counselorInfo.setAvatar(counselor.getAvatar());
        detailVO.setCounselor(counselorInfo);

        SessionDetailVO.UserInfo userInfo = new SessionDetailVO.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        detailVO.setUser(userInfo);

        if (lastMessage != null) {
            detailVO.setLastMessage(lastMessage.getMessageContent());
            detailVO.setLastMessageTime(lastMessage.getMessageSentTime().toString());
        }

        detailVO.setUserHasEvaluated(session.getUserHasEvaluated());
        detailVO.setCounselorHasEvaluated(session.getCounselorHasEvaluated());

        if (session.getSummary() != null) {
            detailVO.setSummary(session.getSummary().getSummaryText());
        }

        return detailVO;
    }

    @Override
    public void startSession(Integer sessionId, Integer counselorId) {
        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("会话不存在"));

        if (!session.getCounselorId().equals(counselorId)) {
            throw new BusinessException("无权操作此会话");
        }

        session.setSessionStatus(ConsultationSession.SessionStatus.IN_PROGRESS);
        sessionRepository.save(session);
    }

    @Override
    public void endSession(Integer sessionId, String summaryText) {
        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("会话不存在"));

        session.setSessionStatus(ConsultationSession.SessionStatus.COMPLETED);
        session.setSessionEndTime(LocalDateTime.now());

        if (StringUtils.isNotBlank(summaryText)) {
            SessionSummary summary = new SessionSummary();
            summary.setSessionId(sessionId);
            summary.setSummaryText(summaryText);
            session.setSummary(summary);
        }

        sessionRepository.save(session);
    }

    @Override
    public ConsultationSession getSessionById(Integer sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("会话不存在"));
    }

    private SessionVO convertToVO(ConsultationSession session) {
        SessionVO vo = new SessionVO();
        vo.setSessionId(session.getSessionId());
        vo.setStartTime(session.getSessionStartTime().toString());
        if (session.getSessionEndTime() != null) {
            vo.setEndTime(session.getSessionEndTime().toString());
        }
        vo.setStatus(session.getSessionStatus().name());
        vo.setCounselorId(session.getCounselorId());
        vo.setUserId(session.getUserId());
        vo.setUserHasEvaluated(session.getUserHasEvaluated());
        vo.setCounselorHasEvaluated(session.getCounselorHasEvaluated());

        // 可以添加更多字段转换
        return vo;
    }
}