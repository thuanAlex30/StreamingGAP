
package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.*;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.service.SongListenStatsService;
import com.fpt.StreamGAP.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.Integer;
@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private SongListenStatsService songListenStatsService;

    @GetMapping
    public ReqRes getAllSongs() {
        List<Song> songs = songService.getAllSongsForCurrentUser();
        List<SongDTO> songDTOs = songs.stream()
                .map(song -> {
                    SongDTO dto = new SongDTO();
                    dto.setSong_id(song.getSongId());
                    dto.setAlbum(song.getAlbum());
                    dto.setTitle(song.getTitle());
                    dto.setGenre(song.getGenre());
                    dto.setDuration(song.getDuration());
                    dto.setAudio_file_url(song.getAudio_file_url());
                    dto.setLyrics(song.getLyrics());
                    dto.setCreated_at(song.getCreated_at());

                    StatisticsDTO stats = songListenStatsService.getStatsBySongId(song.getSongId());
                    dto.setListen_count(stats != null ? stats.getCount() : 0);

                    return dto;
                })
                .collect(Collectors.toList());

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Songs retrieved successfully");
        response.setSongDtoList(songDTOs);

        return response;
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongDetailDTO> getSongDetail(@PathVariable int songId) {
        SongDetailDTO songDetail = songService.getSongDetail(songId);
        if (songDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(songDetail);
    }

    @PostMapping
    public ResponseEntity<ReqRes> createSong(@RequestBody SongDTO songDTO) {
        Song song = new Song();
        song.setTitle(songDTO.getTitle());
        song.setGenre(songDTO.getGenre());
        song.setDuration(songDTO.getDuration());
        song.setAudio_file_url(songDTO.getAudio_file_url());
        song.setLyrics(songDTO.getLyrics());
        song.setAlbum(songDTO.getAlbum());

        Song savedSong = songService.createSong(song);
        SongDTO dto = new SongDTO();
        dto.setSong_id(savedSong.getSongId());
        dto.setAlbum(savedSong.getAlbum());
        dto.setTitle(savedSong.getTitle());
        dto.setGenre(savedSong.getGenre());
        dto.setDuration(savedSong.getDuration());
        dto.setAudio_file_url(savedSong.getAudio_file_url());
        dto.setLyrics(savedSong.getLyrics());
        dto.setCreated_at(savedSong.getCreated_at());

        ReqRes response = new ReqRes();
        response.setStatusCode(201);
        response.setMessage("Song created successfully");
        response.setSongDtoList(List.of(dto));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReqRes> updateSong(@PathVariable Integer id, @RequestBody SongDTO songDTO) {
        Song updatedSong = songService.updateSong(id, songDTO);
        SongDTO dto = new SongDTO();
        dto.setSong_id(updatedSong.getSongId());
        dto.setAlbum(updatedSong.getAlbum());
        dto.setTitle(updatedSong.getTitle());
        dto.setGenre(updatedSong.getGenre());
        dto.setDuration(updatedSong.getDuration());
        dto.setAudio_file_url(updatedSong.getAudio_file_url());
        dto.setLyrics(updatedSong.getLyrics());
        dto.setCreated_at(updatedSong.getCreated_at());
        dto.setListen_count(updatedSong.getListen_count());

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Song updated successfully");
        response.setSongDtoList(List.of(dto));

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ReqRes> deleteSong(@PathVariable Integer id) {
        songService.deleteSong(id);
        ReqRes response = new ReqRes();
        response.setStatusCode(204);
        response.setMessage("Song deleted successfully");
        return ResponseEntity.noContent().build();
    }
//    @GetMapping("/search")
//    public ResponseEntity<ReqRes> searchSongs(@RequestParam String keyword) {
//        List<SongTitleDTO> songTitles = songService.searchSongs(keyword);
//
//        ReqRes response = new ReqRes();
//        response.setStatusCode(HttpStatus.OK.value());
//        response.setMessage("Search successful");
//        response.setSongDtoList(songTitles);;
//
//        return ResponseEntity.ok(response);
//    }

    private SongDetailDTO convertToSongDetailDTO(Song song) {
        SongDetailDTO dto = new SongDetailDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setDuration(song.getDuration());
        dto.setAudioFileUrl(song.getAudio_file_url());
        dto.setLyrics(song.getLyrics());
        return dto;
    }

}
