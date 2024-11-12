package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.SongDetailDTO;
import com.fpt.StreamGAP.dto.SongTitleDTO;
import com.fpt.StreamGAP.entity.*;

import com.fpt.StreamGAP.repository.AlbumRepository;
import com.fpt.StreamGAP.repository.SongListenStatsRepository;
import com.fpt.StreamGAP.repository.SongRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fpt.StreamGAP.dto.SongDTO;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongListenStatsRepository songListenStatsRepository;

    @Autowired
    private UserManagementService userManagementService;

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    private boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Collection<? extends GrantedAuthority> authorities = ((UserDetails) principal).getAuthorities();
            return authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        }
        return false;
    }

    public List<Song> getAllSongsForCurrentUser() {
        try {
            return songRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving songs: " + e.getMessage(), e);
        }
    }

    public Optional<Song> getSongByIdForCurrentUser(Integer id) {
        try {
            return songRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving song by ID: " + e.getMessage(), e);
        }
    }

    public void playSong(Integer id) {
        try {
            Song song = getSongByIdForCurrentUser(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
            List<SongListenStats> statsList = songListenStatsRepository.findBySong(song);
            SongListenStats stats;

            if (statsList.isEmpty()) {
                stats = new SongListenStats();
                stats.setSong(song);
                stats.setListen_count(1);
            } else {
                stats = statsList.get(0);
                stats.setListen_count(stats.getListen_count() + 1);
            }
            songListenStatsRepository.save(stats);
            song.setListen_count(song.getListen_count() + 1);
            songRepository.save(song);
        } catch (Exception e) {
            throw new RuntimeException("Error playing song: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Song createSong(Song song) {
        try {
            String currentUsername = getCurrentUsername();
            if (currentUsername == null) {
                throw new RuntimeException("Người dùng không xác thực");
            }
            song.setCreatedByUsername(currentUsername);
            song.setCreated_at(new Date());
            return songRepository.save(song);
        } catch (Exception e) {
            throw new RuntimeException("Error saving song: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Song updateSong(Integer songId, SongDTO songDetails) {
        try {
            Song existingSong = getSongByIdForCurrentUser(songId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
            if (!isAdmin() && !existingSong.getCreatedByUsername().equals(getCurrentUsername())) {
                throw new RuntimeException("Bạn không có quyền cập nhật bài hát này");
            }
            if (songDetails.getTitle() != null) {
                existingSong.setTitle(songDetails.getTitle());
            }
            if (songDetails.getGenre() != null) {
                existingSong.setGenre(songDetails.getGenre());
            }
            if (songDetails.getDuration() != null) {
                existingSong.setDuration(songDetails.getDuration());
            }
            if (songDetails.getAudio_file_url() != null) {
                existingSong.setAudio_file_url(songDetails.getAudio_file_url());
            }
            if (songDetails.getLyrics() != null) {
                existingSong.setLyrics(songDetails.getLyrics());
            }
            if (songDetails.getAlbum() != null) {
                Album album = albumRepository.findById(songDetails.getAlbum().getAlbumId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Album not found"));
                existingSong.setAlbum(album);
            }

            return songRepository.save(existingSong);
        } catch (Exception e) {
            throw new RuntimeException("Error updating song: " + e.getMessage(), e);
        }
    }

    public void deleteSong(Integer songId) {
        try {
            Song existingSong = songRepository.findById(songId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
            if (isAdmin() || existingSong.getCreatedByUsername().equals(getCurrentUsername())) {

                List<SongListenStats> listenStats = songListenStatsRepository.findBySong(existingSong);
                if (!listenStats.isEmpty()) {
                    songListenStatsRepository.deleteAll(listenStats);
                }
                songRepository.delete(existingSong);
            } else {
                throw new RuntimeException("Bạn không có quyền xóa bài hát này");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting song: " + e.getMessage(), e);
        }
    }
    public List<SongTitleDTO> searchSongs(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return List.of();
            }
            List<Song> songs = songRepository.findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(keyword);
            // Map Song objects to SongTitleDTO
            return songs.stream()
                    .map(song -> new SongTitleDTO(song.getTitle()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching songs: " + e.getMessage(), e);
        }
    }
    public Song findById(int songId) {
        return songRepository.findById(songId).orElse(null); // Tìm bài hát theo ID
    }
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
    public SongDetailDTO getSongDetail(int songId) {
        Song song = findById(songId);
        if (song == null) {
            return null; // Hoặc ném ngoại lệ nếu cần
        }
        return convertToSongDetailDTO(song);
    }
}
