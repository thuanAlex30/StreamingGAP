package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.MusicGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicGameRepository extends JpaRepository<MusicGame, Integer> {
}