package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.dto.UserDTO;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.MusicGameRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
                gameDTO.setId(game.getId());
                gameDTO.setQuestion_text(game.getQuestion_text());
                gameDTO.setAnswer_1(game.getAnswer_1());
                gameDTO.setAnswer_2(game.getAnswer_2());
                gameDTO.setAnswer_3(game.getAnswer_3());
                gameDTO.setAnswer_4(game.getAnswer_4());
                gameDTO.setCorrect_answer(game.getCorrect_answer());
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
        game.setQuestion_text(musicGameDTO.getQuestion_text());
        game.setAnswer_1(musicGameDTO.getAnswer_1());
        game.setAnswer_2(musicGameDTO.getAnswer_2());
        game.setAnswer_3(musicGameDTO.getAnswer_3());
        game.setAnswer_4(musicGameDTO.getAnswer_4());
        game.setCorrect_answer(musicGameDTO.getCorrect_answer());
        return musicGameRepository.save(game);
    }
    public List<MusicGameDTO> importMusicGamesFromExcel(MultipartFile excelFile) {
        List<MusicGameDTO> musicGameDTOList = new ArrayList<>();

        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                // Ensure that there are enough cells in the row
                if (row.getPhysicalNumberOfCells() < 6) {
                    System.out.println("Row " + row.getRowNum() + " is missing some cells.");
                    continue;
                }

                MusicGameDTO musicGameDTO = new MusicGameDTO();
                musicGameDTO.setQuestion_text(row.getCell(0).getStringCellValue());
                musicGameDTO.setAnswer_1(row.getCell(1).getStringCellValue());
                musicGameDTO.setAnswer_2(row.getCell(2).getStringCellValue());
                musicGameDTO.setAnswer_3(row.getCell(3).getStringCellValue());
                musicGameDTO.setAnswer_4(row.getCell(4).getStringCellValue());
                musicGameDTO.setCorrect_answer((int) row.getCell(5).getNumericCellValue());

                // Add basic validation checks if needed
                if (musicGameDTO.getQuestion_text() == null || musicGameDTO.getQuestion_text().isEmpty()) {
                    System.out.println("Question text is missing for row " + row.getRowNum());
                    continue;
                }

                musicGameDTOList.add(musicGameDTO);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return musicGameDTOList;
    }
    @Transactional
    public MusicGame updateMusicGame(Integer id, MusicGameDTO gameDetails) {
        MusicGame existingGame = musicGameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!isAdmin()) {
            throw new RuntimeException("You don't have permission to update this game.");
        }

        // Update existing game details with the new details
        existingGame.setQuestion_text(gameDetails.getQuestion_text()); // Make sure this method matches your field name
        existingGame.setAnswer_1(gameDetails.getAnswer_1()); // Ensure these methods are defined
        existingGame.setAnswer_2(gameDetails.getAnswer_2());
        existingGame.setAnswer_3(gameDetails.getAnswer_3());
        existingGame.setAnswer_4(gameDetails.getAnswer_4());
        existingGame.setCorrect_answer(gameDetails.getCorrect_answer());

        // Save and return the updated game
        return musicGameRepository.save(existingGame);
    }


    public void deleteMusicGame(Integer Id) {
        if (!isAdmin()) {
            throw new RuntimeException("You don't have permission to delete this game.");
        }
        musicGameRepository.deleteById(Id);
    }

    public MusicGameDTO convertToDTO(MusicGame musicGame) {
        MusicGameDTO dto = new MusicGameDTO();
        dto.setId(musicGame.getId());
        dto.setQuestion_text(musicGame.getQuestion_text());
        dto.setAnswer_1(musicGame.getAnswer_1());
        dto.setAnswer_2(musicGame.getAnswer_2());
        dto.setAnswer_3(musicGame.getAnswer_3());
        dto.setAnswer_4(musicGame.getAnswer_4());
        dto.setCorrect_answer(musicGame.getCorrect_answer());
        return dto;
    }
}
