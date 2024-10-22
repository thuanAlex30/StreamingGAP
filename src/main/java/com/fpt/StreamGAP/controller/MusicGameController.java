package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.service.MusicGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/musicgames")
public class MusicGameController {

    @Autowired
    private MusicGameService musicGameService;
    @GetMapping
    public ReqRes getAllMusicGames() {
        ReqRes response = new ReqRes();
        try {
            List<MusicGameDTO> musicGames = musicGameService.getAllMusicGames();
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
        Optional<MusicGameDTO> musicGame = musicGameService.getMusicGameById(id);
        if (musicGame.isPresent()) {
            response.setStatusCode(200);
            response.setMessage("Success");
            List<MusicGameDTO> musicGameList = Collections.singletonList(musicGame.get());
            response.setMusicGameList(musicGameList);
        } else {
            response.setStatusCode(404);
            response.setMessage("Music game not found with ID: " + id);
        }
        return response;
    }
    @PostMapping
    public ReqRes createMusicGame(@RequestBody MusicGame musicGame) {
        ReqRes response = new ReqRes();
        try {
            MusicGame savedGame = musicGameService.saveMusicGame(musicGame);
            response.setStatusCode(201);
            response.setMessage("Music game created successfully");
            List<MusicGameDTO> musicGameList = Collections.singletonList(musicGameService.convertToDTO(savedGame));
            response.setMusicGameList(musicGameList);
        } catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage("Error occurred while creating music game: " + e.getMessage());
        }
        return response;
    }

    // Update an existing music game by ID
    @PutMapping("/{id}")
    public ReqRes updateMusicGame(@PathVariable Integer id, @RequestBody MusicGame musicGame) {
        ReqRes response = new ReqRes();
        Optional<MusicGame> existingGameOpt = musicGameService.getMusicGameEntityById(id);

        if (existingGameOpt.isPresent()) {
            try {
                MusicGame gameToUpdate = existingGameOpt.get();

                // Update fields
                gameToUpdate.setScore(musicGame.getScore());
                gameToUpdate.setGame_type(musicGame.getGame_type());
                gameToUpdate.setUser(musicGame.getUser());

                // Ensure played_at is set to current date
                gameToUpdate.setPlayed_at(new Date());

                // Save updated game
                MusicGame updatedGame = musicGameService.saveMusicGame(gameToUpdate);

                // Prepare response
                response.setStatusCode(200);
                response.setMessage("Music game updated successfully");
                List<MusicGameDTO> musicGameList = Collections.singletonList(musicGameService.convertToDTO(updatedGame));
                response.setMusicGameList(musicGameList);
            } catch (Exception e) {
                response.setStatusCode(404);
                response.setMessage("Error occurred while updating music game: " + e.getMessage());
            }
        } else {
            response.setStatusCode(404);
            response.setMessage("Music game not found with ID: " + id);
        }

        return response;
    }

    // Delete a music game by ID
    @DeleteMapping("/{id}")
    public ReqRes deleteMusicGame(@PathVariable Integer id) {
        ReqRes response = new ReqRes();
        if (musicGameService.getMusicGameById(id).isPresent()) {
            try {
                musicGameService.deleteMusicGame(id);
                response.setStatusCode(200);
                response.setMessage("Music game deleted successfully");
            } catch (Exception e) {
                response.setStatusCode(404);
                response.setMessage("Error occurred while deleting music game: " + e.getMessage());
            }
        } else {
            response.setStatusCode(404);
            response.setMessage("Music game not found with ID: " + id);
        }
        return response;
    }
}