package com.fpt.StreamGAP.controller;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.dto.SongListenStatsDTO;
import com.fpt.StreamGAP.dto.StatisticsDTO;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.SongListenStats;
import com.fpt.StreamGAP.repository.SongListenStatsRepository;
import com.fpt.StreamGAP.repository.SongRepository;
import com.fpt.StreamGAP.service.SongListenStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/songsStats")
public class SongListenStatsController {
    @Autowired
    private SongListenStatsService songListenStatsService;
    @GetMapping
    public ReqRes getAllSongListenStats() {
        List<SongListenStats> statsList = songListenStatsService.getAll();
        List<SongListenStatsDTO> statsDTOs = statsList.stream()
                .map(stats -> {
                    SongListenStatsDTO dto = new SongListenStatsDTO();
                    dto.setListen_id(stats.getListen_id());
                    dto.setSongId(stats.getSong().getSongId());
                    dto.setTitle(stats.getSong().getTitle());
                    dto.setGenre(stats.getSong().getGenre());
                    dto.setListenCount(stats.getListen_count());
                    dto.setRecordedAt(new Date(stats.getRecorded_at().getTime()));
                    return dto;
                })
                .collect(Collectors.toList());
        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Song listen stats retrieved successfully");
        response.setSongListenStatsList(statsDTOs);
        return response;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReqRes> getSongListenStatsById(@PathVariable("id") Integer listenId) {
        SongListenStats stats = songListenStatsService.getById(listenId);
        if (stats == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song listen stats not found");
        }
        SongListenStatsDTO dto = new SongListenStatsDTO();
        dto.setListen_id(stats.getListen_id());
        dto.setSongId(stats.getSong().getSongId());
        dto.setTitle(stats.getSong().getTitle());
        dto.setGenre(stats.getSong().getGenre());
        dto.setListenCount(stats.getListen_count());
        dto.setRecordedAt(new Date(stats.getRecorded_at().getTime()));
        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Song listen stats retrieved successfully");
        response.setSongListenStatsList(List.of(dto));
        return ResponseEntity.ok(response);
    }
}