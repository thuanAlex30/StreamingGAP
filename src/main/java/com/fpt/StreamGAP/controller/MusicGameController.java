package com.fpt.StreamGAP.controller;


import com.fpt.StreamGAP.data.MusicGameImporter;
import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.service.MusicGameService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/musicgames")
public class MusicGameController {

    @Autowired
    private MusicGameService musicGameService;
    private MusicGameImporter musicGameImporter;

    @GetMapping
    public ReqRes getAllMusicGames(Integer gameId) {
        ReqRes response = new ReqRes();
        try {
            List<MusicGameDTO> musicGames = musicGameService.getAll();
            response.setStatusCode(200);
            response.setMessage("Success");
            response.setMusicGameList(musicGames);
        } catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage("Error occurred while fetching music games: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/{id}")
    public ReqRes getMusicGameById(@PathVariable Integer id) {
        ReqRes response = new ReqRes();
        try {
            Optional<MusicGame> musicGame = musicGameService.getMusicGameById(id);
            if (musicGame.isPresent()) {
                response.setStatusCode(200);
                response.setMessage("Success");
                response.setMusicGameList(Collections.singletonList(musicGameService.convertToDTO(musicGame.get())));
            } else {
                response.setStatusCode(404);
                response.setMessage("Music game not found with ID: " + id);
            }
        } catch (Exception e) {
            response.setStatusCode(500); // Handle unexpected exceptions
            response.setMessage("Error occurred while fetching music game: " + e.getMessage());
        }
        return response;
    }
    @PostMapping
    public ReqRes createMusicGame(@RequestBody List<MusicGameDTO> musicGameDTOs, Principal principal) {
        ReqRes response = new ReqRes();
        List<MusicGameDTO> createdGames = new ArrayList<>();

        try {
            // In ra giá trị của musicGameDTOs để kiểm tra
            System.out.println("Received musicGameDTOs: " + musicGameDTOs);

            for (MusicGameDTO musicGameDTO : musicGameDTOs) {
                MusicGame savedGame = musicGameService.createMusicGame(musicGameDTO);
                MusicGameDTO createdGameDTO = musicGameService.convertToDTO(savedGame);
                createdGames.add(createdGameDTO);
            }

            response.setStatusCode(201);
            response.setMessage("Music game(s) created successfully");
            response.setMusicGameList(createdGames);

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Error occurred while creating music game(s): " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
        }

        return response;
    }



    @PostMapping("/importandcreate")
    public ReqRes importAndCreateMusicGames(@RequestParam("file") MultipartFile file, Principal principal) {
        ReqRes response = new ReqRes();
        List<MusicGameDTO> createdGames = new ArrayList<>();

        try {
            if (principal == null) {
                throw new IllegalArgumentException("User must be authenticated.");
            }

            // Check if the file is empty
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File must be provided.");
            }

            // Import and process MusicGame data from Excel
            List<MusicGameDTO> musicGames = importMusicGamesFromExcel(file);

            // Save each game to the database using musicGameService
            for (MusicGameDTO game : musicGames) {
                if (game != null) {
                    MusicGame savedGame = musicGameService.createMusicGame(game);
                    createdGames.add(musicGameService.convertToDTO(savedGame));
                    System.out.println("Music game created: " + game);
                } else {
                    System.out.println("Invalid game entry encountered.");
                }
            }

            // Set the success response
            response.setStatusCode(201);
            response.setMessage("Music games imported and created successfully.");
            response.setMusicGameList(createdGames);

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Error occurred while creating music games: " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
        }

        return response;
    }

    public List<MusicGameDTO> importMusicGamesFromExcel(MultipartFile excelFile) {
        List<MusicGameDTO> musicGameDTOList = new ArrayList<>();

        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                MusicGameDTO musicGameDTO = new MusicGameDTO();
                musicGameDTO.setQuestion_text(row.getCell(0).getStringCellValue());
                musicGameDTO.setAnswer_1(row.getCell(1).getStringCellValue());
                musicGameDTO.setAnswer_2(row.getCell(2).getStringCellValue());
                musicGameDTO.setAnswer_3(row.getCell(3).getStringCellValue());
                musicGameDTO.setAnswer_4(row.getCell(4).getStringCellValue());
                musicGameDTO.setCorrect_answer((int) row.getCell(5).getNumericCellValue());
                musicGameDTOList.add(musicGameDTO);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return musicGameDTOList;
    }

    @PutMapping("/{id}")
    public ReqRes updateMusicGame(@PathVariable Integer id, @RequestBody MusicGameDTO musicGameDTO) {
        ReqRes response = new ReqRes();
        try {
            MusicGame updatedGame = musicGameService.updateMusicGame(id, musicGameDTO);
            response.setStatusCode(200);
            response.setMessage("Music game updated successfully");
            response.setMusicGameList(Collections.singletonList(musicGameService.convertToDTO(updatedGame)));
        } catch (Exception e) {
            response.setStatusCode(400); // Handle bad request during update
            response.setMessage("Error occurred while updating music game: " + e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ReqRes deleteMusicGame(@PathVariable Integer id) {
        ReqRes response = new ReqRes();
        try {
            Optional<MusicGame> musicGameOpt = musicGameService.getMusicGameById(id);
            if (musicGameOpt.isPresent()) {
                musicGameService.deleteMusicGame(id);
                response.setStatusCode(200);
                response.setMessage("Music game deleted successfully");
            } else {
                response.setStatusCode(404);
                response.setMessage("Music game not found with ID: " + id);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting music game: " + e.getMessage());
        }
        return response;
    }
}