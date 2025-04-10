package com.example.demo.Queue.mapper;

import com.example.demo.Queue.entity.Queue;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface QueueMapper {
    int insert(Queue queue);
    Queue selectById(Integer queueId);
    List<Queue> selectByUserId(Integer userId);
    List<Queue> selectCurrentQueue(Integer counselorId);
    List<Queue> selectActiveQueues();
    int updateById(Queue queue);
    int countAheadUsers(@Param("counselorId") Integer counselorId,
                        @Param("currentNumber") Integer currentNumber,
                        @Param("joinTime") LocalDateTime joinTime);
    Queue selectNextPatient(Integer counselorId);
    Integer selectLastQueueNumberForUpdate(Integer counselorId);
    int countByStatus(@Param("counselorId") Integer counselorId,
                      @Param("status") String status);
    Integer getCounselorIdByQueueId(Integer queueId);

}