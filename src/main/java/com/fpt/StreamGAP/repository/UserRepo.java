package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}
