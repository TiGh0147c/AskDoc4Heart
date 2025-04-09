package com.example.demo.RegisterAndLogin.Mapper;

import com.example.demo.RegisterAndLogin.Entity.Counselor;
import com.example.demo.RegisterAndLogin.Entity.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupervisorRepository extends CrudRepository<Supervisor,Integer> {
}
