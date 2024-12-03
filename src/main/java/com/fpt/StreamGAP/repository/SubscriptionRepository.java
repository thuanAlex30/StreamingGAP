package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findByName(String name);
}
