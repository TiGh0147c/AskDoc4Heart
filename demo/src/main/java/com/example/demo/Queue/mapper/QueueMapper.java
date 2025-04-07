package com.example.demo.Queue.mapper;

import com.example.demo.Queue.entity.Queue;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QueueMapper {
    int insert(Queue queue);
    Queue selectById(Integer queueId);
    List<Queue> selectByUserId(Integer userId);//选择用户当前的队列
    List<Queue> selectCurrentQueue(Integer counselorId);//选择指定咨询师当前正在处理的所有队列条目。
    List<Queue> selectActiveQueues();//选择所有处于活动状态的队列条目
    int updateById(Queue queue);//根据队列ID更新队列条目的信息

    /**
     * 查询指定咨询师的所有队列条目，包含已取消的用户。
     */
    List<Queue> selectAllQueuesForCounselor(@Param("counselorId") Integer counselorId);
    /**
     * 查询所有的队列条目，包括已取消的用户。
     */
    List<Queue> selectAllQueues();
    /**
     * 计算指定用户前面有多少人在等待（仅包括waiting状态）。
     */

    int countAheadUsers(@Param("counselorId") Integer counselorId,
                        @Param("queueNumber") Integer queueNumber,
                        @Param("joinQueueTime") LocalDateTime joinQueueTime,
                        @Param("statuses") List<String> statuses);
    //计算在指定咨询师、当前编号和加入时间之前的排队人数。
    Queue selectNextPatient(Integer counselorId);// 选择下一个需要处理的队列条目
    Integer selectLastQueueNumberForUpdate(Integer counselorId);//选择并锁定最后一个队列编号以便更新。
    /**
     * 清空所有队列条目。
     */
    @Delete("DELETE FROM queue")
    void clearAllQueues();

    /**
     * 根据咨询师ID清空队列条目。
     *
     * @param counselorId 咨询师ID
     */
    @Delete("DELETE FROM queue WHERE counselor_id = #{counselorId}")
    void clearQueueByCounselorId(@Param("counselorId") Integer counselorId);
    //统计指定咨询师的所有排队人数
    @Select("SELECT COUNT(*) FROM queue WHERE counselor_id = #{counselorId} AND queue_status IN ('waiting')")
    int countTotalWaitingUsers(@Param("counselorId") Integer counselorId);
}