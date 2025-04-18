package com.example.demo.Evaluation.Mapper;

import com.example.demo.Evaluation.Entity.CounselorEvaluation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselorEvaluationRepository extends CrudRepository<CounselorEvaluation,Integer> {

}
