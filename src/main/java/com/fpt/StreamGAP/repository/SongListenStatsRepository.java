package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.SongListenStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SongListenStatsRepository extends JpaRepository<SongListenStats, Integer> {
    List<SongListenStats> findBySong(Song song);
}
