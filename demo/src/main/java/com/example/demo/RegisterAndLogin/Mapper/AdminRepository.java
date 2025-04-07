package com.example.demo.RegisterAndLogin.Mapper;

import com.example.demo.RegisterAndLogin.Entity.Administrator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Administrator,Integer> {
    Optional<Administrator> findByUsername(String username);
}
