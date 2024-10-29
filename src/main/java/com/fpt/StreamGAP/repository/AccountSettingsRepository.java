package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.AccountSettings;
import com.fpt.StreamGAP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountSettingsRepository extends JpaRepository<AccountSettings, Integer> {
    Optional<AccountSettings> findByUser(User user);
}
