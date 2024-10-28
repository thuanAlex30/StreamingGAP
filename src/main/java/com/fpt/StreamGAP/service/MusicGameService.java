package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.dto.UserDTO;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.MusicGameRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MusicGameService {

    @Autowired
    private MusicGameRepository musicGameRepository;

    @Autowired
    private UserRepo userRepository;

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

    private Integer getUserIdFromCurrentUsername() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(User::getUser_id).orElse(null);
    }

    public List<MusicGameDTO> getAll() {
        List<MusicGame> games;
        List<MusicGameDTO> gameDTOs = new ArrayList<>();

        try {
            games = musicGameRepository.findAll();
            for (MusicGame game : games) {
                MusicGameDTO gameDTO = new MusicGameDTO();
                gameDTO.setGameId(game.getGameId());
                gameDTO.setUsername(game.getCreatedByUsername());
                gameDTO.setScore(game.getScore());
                gameDTO.setQuestion_text(game.getQuestion_text());
                gameDTO.setAnswer_1(game.getAnswer_1());
                gameDTO.setAnswer_2(game.getAnswer_2());
                gameDTO.setAnswer_3(game.getAnswer_3());
                gameDTO.setAnswer_4(game.getAnswer_4());
                gameDTO.setCorrect_answer(game.getCorrect_answer());
                gameDTO.setUser_answer(game.getUser_answer());
                gameDTO.setPlayedAt(game.getPlayed_at());
                gameDTO.setGame_type(game.getGame_type());
                gameDTOs.add(gameDTO);
            }


            return gameDTOs;

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving music games for user", e);
        }
    }

    public Optional<MusicGame> getMusicGameById(Integer id) {
        return musicGameRepository.findById(id);
    }

    @Transactional
    public MusicGame createMusicGame(MusicGameDTO musicGameDTO) {
        MusicGame game = new MusicGame();

        // Map data from DTO to entity
        game.setQuestion_text(musicGameDTO.getQuestion_text());
        game.setCreatedByUsername(musicGameDTO.getUsername());
        game.setGame_type(musicGameDTO.getGame_type());
        game.setAnswer_1(musicGameDTO.getAnswer_1());
        game.setAnswer_2(musicGameDTO.getAnswer_2());
        game.setAnswer_3(musicGameDTO.getAnswer_3());
        game.setAnswer_4(musicGameDTO.getAnswer_4());
        game.setCorrect_answer(musicGameDTO.getCorrect_answer());
        game.setUser_answer(musicGameDTO.getUser_answer());
         // Assuming there's a game type

        // Set the score to 0 if not provided
        game.setScore(musicGameDTO.getScore() != null ? musicGameDTO.getScore() : 0);

        // Save the game using the repository and return the saved entity
        return musicGameRepository.save(game);
    }
//    @Transactional
//    public MusicGame createMusicGame(MusicGameDTO musicGameDTO) {
//        MusicGame game = new MusicGame();
//
//        // Chỉ map giá trị nếu không phải là null
//        if (musicGameDTO.getQuestion_text() != null) {
//            game.setQuestion_text(musicGameDTO.getQuestion_text());
//        }
//        if (musicGameDTO.getUsername() != null) {
//            game.setCreatedByUsername(musicGameDTO.getUsername());
//        }
//        if (musicGameDTO.getGame_type() != null) {
//            game.setGame_type(musicGameDTO.getGame_type());
//        }
//        if (musicGameDTO.getAnswer_1() != null) {
//            game.setAnswer_1(musicGameDTO.getAnswer_1());
//        }
//        if (musicGameDTO.getAnswer_2() != null) {
//            game.setAnswer_2(musicGameDTO.getAnswer_2());
//        }
//        if (musicGameDTO.getAnswer_3() != null) {
//            game.setAnswer_3(musicGameDTO.getAnswer_3());
//        }
//        if (musicGameDTO.getAnswer_4() != null) {
//            game.setAnswer_4(musicGameDTO.getAnswer_4());
//        }
//        if (musicGameDTO.getCorrect_answer() != null) {
//            game.setCorrect_answer(musicGameDTO.getCorrect_answer());
//        }
//        if (musicGameDTO.getUser_answer() != null) {
//            game.setUser_answer(musicGameDTO.getUser_answer());
//        }
//
//        // Set điểm mặc định là 0 nếu không có điểm (null)
//        game.setScore(musicGameDTO.getScore() != null ? musicGameDTO.getScore() : 0);
//
//        // Lưu vào repository và trả về thực thể đã lưu
//        return musicGameRepository.save(game);
//    }

    @Transactional
    public MusicGame updateMusicGame(Integer gameId, MusicGameDTO gameDetails) {
        MusicGame existingGame = musicGameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!isAdmin()) {
            throw new RuntimeException("You don't have permission to update this game.");
        }

        if (gameDetails.getScore() != null) {
            existingGame.setScore(gameDetails.getScore());
        }
        if (gameDetails.getGame_type() != null) {
            existingGame.setGame_type(gameDetails.getGame_type());
        }


        return musicGameRepository.save(existingGame);
    }

    public void deleteMusicGame(Integer gameId) {
        if (!isAdmin()) {
            throw new RuntimeException("You don't have permission to delete this game.");
        }
        musicGameRepository.deleteById(gameId);
    }

//    public void playMusicGame(Integer gameId, Integer userAnswer) {
//        MusicGame game = musicGameRepository.findById(gameId)
//                .orElseThrow(() -> new RuntimeException("Game not found"));
//
//        Integer currentUserId = getUserIdFromCurrentUsername();
//        if (!game.getUser().getUser_id().equals(currentUserId)) {
//            throw new RuntimeException("You are not allowed to play this game.");
//        }
//
//        game.setUser_answer(userAnswer);
//        if (userAnswer.equals(game.getCorrect_answer())) {
//            game.setScore(game.getScore() + 1);
//        }
//        musicGameRepository.save(game);
//    }

    public MusicGameDTO convertToDTO(MusicGame musicGame) {
        MusicGameDTO dto = new MusicGameDTO();
        dto.setGameId(musicGame.getGameId());
        if (musicGame.getCreatedByUsername() != null) {
            dto.setUsername(musicGame.getCreatedByUsername());
        } else {
            dto.setUsername(null);
        }
        dto.setScore(musicGame.getScore());
        dto.setGame_type(musicGame.getGame_type());
        dto.setQuestion_text(musicGame.getQuestion_text());
        dto.setUsername(musicGame.getCreatedByUsername());
        dto.setAnswer_1(musicGame.getAnswer_1());
        dto.setAnswer_2(musicGame.getAnswer_2());
        dto.setAnswer_3(musicGame.getAnswer_3());
        dto.setAnswer_4(musicGame.getAnswer_4());
        dto.setCorrect_answer(musicGame.getCorrect_answer());
        dto.setUser_answer(musicGame.getUser_answer());
        dto.setPlayedAt(musicGame.getPlayed_at());
        return dto;
    }
}
