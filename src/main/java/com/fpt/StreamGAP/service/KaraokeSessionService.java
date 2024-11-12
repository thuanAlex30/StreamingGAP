package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.KaraokeSessionDTO;
import com.fpt.StreamGAP.entity.KaraokeSession;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.KaraokeSessionRepository;
import com.fpt.StreamGAP.repository.SongRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KaraokeSessionService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private KaraokeSessionRepository karaokeSessionRepository;

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    public List<KaraokeSessionDTO> getAllKaraokeSessions() {
        String currentUsername = getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername).orElse(null);

        if (user == null) {
            return List.of();
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {

            List<KaraokeSession> sessions = karaokeSessionRepository.findAll();
            return sessions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            List<KaraokeSession> sessions = karaokeSessionRepository.findByUser(user);
            return sessions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }


    public KaraokeSessionDTO createKaraokeSession(KaraokeSessionDTO karaokeSessionDTO) {
        User user = userRepository.findByUsername(getCurrentUsername()).orElse(null);
        Song song = songRepository.findById(karaokeSessionDTO.getSongId()).orElse(null);

        if (user == null || song == null) {
            return null;
        }

        KaraokeSession karaokeSession = new KaraokeSession();
        karaokeSession.setUser(user);
        karaokeSession.setSong(song);
        karaokeSession.setRecording_url(karaokeSessionDTO.getRecordingUrl());
        karaokeSession.setCreated_at(new Date(System.currentTimeMillis()));

        KaraokeSession savedSession = karaokeSessionRepository.save(karaokeSession);
        return convertToDTO(savedSession);
    }

    public KaraokeSessionDTO updateKaraokeSession(Integer sessionId, KaraokeSessionDTO karaokeSessionDTO) {
        Optional<KaraokeSession> sessionOptional = karaokeSessionRepository.findById(sessionId);
        if (sessionOptional.isPresent()) {
            KaraokeSession session = sessionOptional.get();
            if (!session.getUser().getUsername().equals(getCurrentUsername())) {
                return null; // Người dùng không sở hữu phiên karaoke này
            }
            session.setRecording_url(karaokeSessionDTO.getRecordingUrl());
            session.setCreated_at(new Date(System.currentTimeMillis()));
            session = karaokeSessionRepository.save(session);
            return convertToDTO(session);
        }
        return null;
    }

    public boolean deleteKaraokeSession(Integer sessionId) {
        Optional<KaraokeSession> sessionOptional = karaokeSessionRepository.findById(sessionId);
        if (sessionOptional.isPresent() && sessionOptional.get().getUser().getUsername().equals(getCurrentUsername())) {
            karaokeSessionRepository.deleteById(sessionId);
            return true;
        }
        return false;
    }

    public Optional<KaraokeSessionDTO> getKaraokeSessionById(Integer sessionId) {
        Optional<KaraokeSession> session = karaokeSessionRepository.findById(sessionId);
        if (session.isPresent() && session.get().getUser().getUsername().equals(getCurrentUsername())) {
            return session.map(this::convertToDTO);
        }
        return Optional.empty();
    }

    private KaraokeSessionDTO convertToDTO(KaraokeSession session) {
        KaraokeSessionDTO dto = new KaraokeSessionDTO();
        dto.setSessionId(session.getSession_id());
        dto.setUserId(session.getUser().getUser_id());
        dto.setSongId(session.getSong().getSongId());
        dto.setRecordingUrl(session.getRecording_url());
        dto.setCreatedAt(session.getCreated_at());
        return dto;
    }
}
