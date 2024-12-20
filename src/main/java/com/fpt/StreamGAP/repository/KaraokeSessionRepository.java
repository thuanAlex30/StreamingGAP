package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.KaraokeSession;
import com.fpt.StreamGAP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KaraokeSessionRepository extends JpaRepository<KaraokeSession, Integer> {
    List<KaraokeSession> findByUser(User user);
    Optional<KaraokeSession> findBySong_SongId(Integer songId);
}
