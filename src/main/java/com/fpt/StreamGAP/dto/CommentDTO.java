package com.fpt.StreamGAP.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CommentDTO {
    private Integer commentId;
    private Integer userId;
    private Integer songId;
    private String username;
    private String content;
    private Date createdAt;
}

