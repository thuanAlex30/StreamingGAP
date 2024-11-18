package com.fpt.StreamGAP.dto;

import lombok.Data;

@Data

public class SongDetailDTO {
    private int songId;
    private String title;
    private String genre;
    private int duration;
    private String audioFileUrl;
    private String lyrics;
    private AlbumsDTO album;
    private Integer listen_count;

    // Getters and Setters
}
