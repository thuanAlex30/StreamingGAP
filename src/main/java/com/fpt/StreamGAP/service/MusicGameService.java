package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.repository.MusicGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
@Service
public class MusicGameService {

    @Autowired
    private MusicGameRepository musicGameRepository;

    public List<MusicGameDTO> getAllMusicGames() {
        return musicGameRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MusicGameDTO> getMusicGameById(Integer id) {
        return musicGameRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<MusicGame> getMusicGameEntityById(Integer id) {
        return musicGameRepository.findById(id);
    }

    public MusicGame saveMusicGame(MusicGame musicGame) {

        if (musicGame.getPlayed_at() == null) {
            musicGame.setPlayed_at(new Date());
        }
        return musicGameRepository.save(musicGame);
    }

    public void deleteMusicGame(Integer id) {
        musicGameRepository.deleteById(id);
    }

    public MusicGameDTO convertToDTO(MusicGame musicGame) {
        MusicGameDTO dto = new MusicGameDTO();
        dto.setGameId(musicGame.getGame_id());
        dto.setUserId(musicGame.getUser().getUser_id());
        dto.setScore(musicGame.getScore());
        dto.setGameType(musicGame.getGame_type());
        dto.setPlayedAt(musicGame.getPlayed_at());
        return dto;
    }
}
