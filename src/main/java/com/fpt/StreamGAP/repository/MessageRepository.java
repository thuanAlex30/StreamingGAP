package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
