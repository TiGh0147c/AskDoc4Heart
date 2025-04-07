package com.example.demo.RegisterAndLogin.Mapper;
import com.example.demo.RegisterAndLogin.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    Optional<User> findByUsername(String username);
}