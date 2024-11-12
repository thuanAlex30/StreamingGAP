package com.fpt.StreamGAP.dto;

import lombok.Data;

import java.sql.Date;
@Data
public class SongListenStatsDTO {
    private Integer listen_id;
    private String title;
    private String genre;
    private Integer songId;
    private int listenCount;
    private Date recordedAt;

    public SongListenStatsDTO() {
    }


}
