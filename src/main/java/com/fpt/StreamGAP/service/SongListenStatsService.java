package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.StatisticsDTO;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.SongListenStats;
import com.fpt.StreamGAP.repository.SongListenStatsRepository;
import com.fpt.StreamGAP.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Date;
import java.util.List;


@Service
public class SongListenStatsService {
    @Autowired
    private SongListenStatsRepository songListenStatsRepository;

    @Autowired
    private SongRepository songRepository;

    public List<SongListenStats> getAll() {
        return songListenStatsRepository.findAll();
    }

    public SongListenStats getById(Integer listenId) {
        return songListenStatsRepository.findById(listenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song Listen Stats not found"));
    }

    public SongListenStats recordListen(Integer songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
        SongListenStats stats = songListenStatsRepository.findBySong(song).stream().findFirst()
                .orElse(null);
        if (stats == null) {
            stats = new SongListenStats();
            stats.setSong(song);
            stats.setListen_count(1);
            stats.setRecorded_at(new Date(System.currentTimeMillis()));
        } else {
            stats.setListen_count(stats.getListen_count() + 1);
            stats.setRecorded_at(new Date(System.currentTimeMillis()));
        }

        return songListenStatsRepository.save(stats);
    }
    public StatisticsDTO getStatsBySongId(Integer songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
        List<SongListenStats> statsList = songListenStatsRepository.findBySong(song);
        StatisticsDTO dto = new StatisticsDTO();
        dto.setLabel("Listen count");
        if (!statsList.isEmpty()) {
            int totalCount = statsList.stream()
                    .mapToInt(SongListenStats::getListen_count)
                    .sum();
            dto.setCount(totalCount);
        } else {
            dto.setCount(0);
        }

        return dto;
    }


}