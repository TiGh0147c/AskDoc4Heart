package com.example.demo.Message.Repository;


import com.example.demo.profilemanagement.entity.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, Integer> {

    // 根据状态查找咨询师
    List<Counselor> findByStatus(Counselor.Status status);

    // 根据专业领域查找
    List<Counselor> findByExpertiseAreaContaining(String expertiseArea);

    // 根据邮箱查找
    Optional<Counselor> findByEmail(String email);

    // 查找可用的督导(isSupervisor=true)
    List<Counselor> findByIsSupervisorAndStatus(
            Boolean isSupervisor,
            Counselor.Status status);
}