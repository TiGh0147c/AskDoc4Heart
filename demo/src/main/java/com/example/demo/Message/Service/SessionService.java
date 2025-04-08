package com.example.demo.Message.Service;
import com.example.demo.Message.DTO.*;
import com.example.demo.Message.Entity.ConsultationSession;

public interface SessionService {

    /**
     * 获取会话列表
     * @param queryDTO 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<SessionVO> getSessionList(SessionQueryDTO queryDTO, PageQuery pageQuery);

    /**
     * 创建新会话
     * @param createDTO 创建参数
     * @return 新会话ID
     */
    Integer createSession(SessionCreateDTO createDTO);

    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @return 会话详情
     */
    SessionDetailVO getSessionDetail(Integer sessionId);

    /**
     * 开始咨询会话
     * @param sessionId 会话ID
     * @param counselorId 咨询师ID
     */
    void startSession(Integer sessionId, Integer counselorId);

    /**
     * 结束咨询会话
     * @param sessionId 会话ID
     * @param summaryText 会话摘要
     */
    void endSession(Integer sessionId, String summaryText);

    /**
     * 获取会话实体(内部使用)
     * @param sessionId 会话ID
     * @return 会话实体
     */
    ConsultationSession getSessionById(Integer sessionId);
}