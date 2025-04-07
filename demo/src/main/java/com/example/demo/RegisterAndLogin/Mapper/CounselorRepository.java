package com.example.demo.RegisterAndLogin.Mapper;

import com.example.demo.RegisterAndLogin.Entity.Counselor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounselorRepository extends CrudRepository<Counselor,Integer> {
    Optional<Counselor> findByName(String name);
}
