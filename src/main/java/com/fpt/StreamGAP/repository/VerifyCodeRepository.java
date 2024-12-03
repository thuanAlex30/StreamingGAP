package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.entity.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VerifyCodeRepository extends JpaRepository<VerifyCode,Integer> {
    VerifyCode findByUser(User user);
    VerifyCode findByCode(String code);
    void deleteByCode(String code);
    // Custom query to find expired verification codes
    List<VerifyCode> findByExpireAtBefore(LocalDateTime now);

    // Custom query to delete expired verification codes
    void deleteByExpireAtBefore(LocalDateTime now);
}
