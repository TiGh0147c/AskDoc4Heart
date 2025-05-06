package com.example.demo.Evaluation.Mapper;

import com.example.demo.Evaluation.Entity.UserEvaluation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEvaluationRepository extends CrudRepository<UserEvaluation,Integer> {
    UserEvaluation findUserEvaluationBySessionId(int id);
    
    // 根据会话ID列表查询用户评价
    List<UserEvaluation> findBySessionIdIn(List<Integer> sessionIds);
}
