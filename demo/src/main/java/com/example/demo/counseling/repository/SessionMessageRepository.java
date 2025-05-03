package com.example.demo.counseling.repository;

import com.example.demo.counseling.model.entity.SessionMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionMessageRepository extends JpaRepository<SessionMessage, Long> {
}