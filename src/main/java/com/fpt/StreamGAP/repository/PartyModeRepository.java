package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.PartyMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyModeRepository extends JpaRepository<PartyMode, Integer> {

}