package com.example.demo.Message.Repository;

import com.example.demo.RegisterAndLogin.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 根据unionId查找用户
    Optional<User> findByUnionId(String unionId);

    // 根据openId查找用户
    Optional<User> findByOpenId(String openId);

    // 根据手机号查找用户
    Optional<User> findByPhoneNumber(String phoneNumber);
}
