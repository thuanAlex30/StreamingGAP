package com.fpt.StreamGAP.dto;

import com.fpt.StreamGAP.entity.Album;
import lombok.Data;

import java.util.Date;

@Data
public class SongDTO {
    private Integer song_id;
    private Album album;
    private String title;
    private String genre;
    private Integer duration;
    private String audio_file_url;
    private String lyrics;
    private Date created_at;
    private Integer listen_count;
    private String recording_Url;
}

