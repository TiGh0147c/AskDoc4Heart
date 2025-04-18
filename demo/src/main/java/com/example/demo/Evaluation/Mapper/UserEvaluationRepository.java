package com.example.demo.Evaluation.Mapper;

import com.example.demo.Evaluation.Entity.UserEvaluation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEvaluationRepository extends CrudRepository<UserEvaluation,Integer> {
    UserEvaluation findUserEvaluationBySessionId(int id);
}
