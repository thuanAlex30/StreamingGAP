package com.fpt.StreamGAP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class YouTubeSearchService {

    private final RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String apiKey;

    private final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    public YouTubeSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> searchVideos(String query) {
        String url = SEARCH_URL + "?part=snippet&type=video&q=" + query + "&key=" + apiKey;

        // Gửi yêu cầu GET đến API YouTube
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, String>> results = new ArrayList<>();
        if (response != null && response.containsKey("items")) {
            List<?> items = (List<?>) response.get("items");

            for (Object itemObj : items) {
                Map<String, Object> item = (Map<String, Object>) itemObj; // Ép kiểu từng phần tử
                Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                Map<String, Object> id = (Map<String, Object>) item.get("id");

                if (id != null) {
                    String videoId = (String) id.get("videoId");

                    if (videoId != null && snippet != null) {
                        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                        Map<String, Object> defaultThumbnail = (Map<String, Object>) thumbnails.get("default");

                        results.add(Map.of(
                                "title", (String) snippet.get("title"),
                                "description", (String) snippet.get("description"),
                                "thumbnail", (String) defaultThumbnail.get("url"),
                                "videoUrl", "https://www.youtube.com/watch?v=" + videoId
                        ));
                    }
                }
            }
        }
        return results;
    }
}
