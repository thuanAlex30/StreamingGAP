package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.service.MusicGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/musicgames")
public class MusicGameController {

    @Autowired
    private MusicGameService musicGameService;
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
    public ReqRes createMusicGame(@RequestBody List<MusicGameDTO> questions, Principal principal) {
        ReqRes response = new ReqRes();
        List<MusicGameDTO> createdGames = new ArrayList<>();
        try {
            String username = validateUserAuthentication(principal);
            for (MusicGameDTO dto : questions) {
                dto.setUsername(username);
                MusicGame savedGame = musicGameService.createMusicGame(dto);
                createdGames.add(musicGameService.convertToDTO(savedGame));
            }
            response.setStatusCode(201);
            response.setMessage("Music games created successfully");
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
    private String validateUserAuthentication(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        return principal.getName();
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