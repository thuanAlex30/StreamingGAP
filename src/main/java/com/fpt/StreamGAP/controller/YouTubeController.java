package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.service.YouTubeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/youtube")
public class YouTubeController {
    @Autowired
    private YouTubeSearchService youTubeSearchService;

    @GetMapping("/search")
    public ResponseEntity<ReqRes> searchVideos(@RequestParam String query) {
        // Thêm từ khóa "karaoke" vào cuối query
        String searchQuery = query + " karaoke";

        // Thực hiện tìm kiếm với query mới
        List<Map<String, String>> results = youTubeSearchService.searchVideos(searchQuery);
        ReqRes response = new ReqRes();

        if (results.isEmpty()) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("No videos found for the given query.");
        } else {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Videos retrieved successfully.");
        }

        response.setVideoList(results);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
