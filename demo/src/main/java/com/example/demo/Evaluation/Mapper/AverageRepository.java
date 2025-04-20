package com.example.demo.Evaluation.Mapper;

import com.example.demo.Evaluation.Entity.Average;
import com.example.demo.Evaluation.Entity.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageRepository extends CrudRepository<Average,Integer> {
    Average findAverageByCounselorId(int counselorId);
}
