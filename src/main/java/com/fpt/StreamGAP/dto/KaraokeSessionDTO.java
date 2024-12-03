package com.fpt.StreamGAP.dto;

import lombok.Data;

import java.util.Date;

@Data
public class KaraokeSessionDTO {
    private Integer sessionId;
    private Integer userId;
    private Integer songId;
    private String recordingUrl;
    private Date createdAt;

    // Constructor mặc định
    public KaraokeSessionDTO() {
    }

    // Constructor chấp nhận tham số kiểu String
    public KaraokeSessionDTO(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }
}
