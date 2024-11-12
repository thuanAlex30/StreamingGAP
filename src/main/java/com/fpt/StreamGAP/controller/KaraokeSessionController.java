package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.KaraokeSessionDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.service.KaraokeSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/karaoke-sessions")
public class KaraokeSessionController {

    @Autowired
    private KaraokeSessionService karaokeSessionService;

    @GetMapping
    public ResponseEntity<ReqRes> getAllKaraokeSessions() {
        List<KaraokeSessionDTO> sessions = karaokeSessionService.getAllKaraokeSessions();

        ReqRes response = new ReqRes();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Karaoke sessions retrieved successfully.");
        response.setKaraokeSessionList(sessions);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReqRes> createKaraokeSession(@RequestBody KaraokeSessionDTO karaokeSessionDTO) {
        KaraokeSessionDTO createdSession = karaokeSessionService.createKaraokeSession(karaokeSessionDTO);

        if (createdSession == null) {
            ReqRes response = new ReqRes();
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Song not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ReqRes response = new ReqRes();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Karaoke session created successfully.");
        response.setKaraokeSessionList(List.of(createdSession));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<ReqRes> updateKaraokeSession(@PathVariable Integer sessionId, @RequestBody KaraokeSessionDTO karaokeSessionDTO) {
        KaraokeSessionDTO updatedSession = karaokeSessionService.updateKaraokeSession(sessionId, karaokeSessionDTO);

        ReqRes response = new ReqRes();
        if (updatedSession == null) {
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            response.setMessage("You do not own this karaoke session or session not found.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Karaoke session updated successfully.");
        response.setKaraokeSessionList(List.of(updatedSession));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ReqRes> deleteKaraokeSession(@PathVariable Integer sessionId) {
        boolean isDeleted = karaokeSessionService.deleteKaraokeSession(sessionId);

        ReqRes response = new ReqRes();
        if (!isDeleted) {
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            response.setMessage("You do not own this karaoke session or session not found.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Karaoke session deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ReqRes> getKaraokeSessionById(@PathVariable Integer sessionId) {
        Optional<KaraokeSessionDTO> session = karaokeSessionService.getKaraokeSessionById(sessionId);

        ReqRes response = new ReqRes();
        if (session.isPresent()) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Karaoke session retrieved successfully.");
            response.setKaraokeSessionList(List.of(session.get()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Karaoke session not found or you do not own this session.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
