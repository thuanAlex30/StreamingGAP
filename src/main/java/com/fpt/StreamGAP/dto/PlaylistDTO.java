package com.fpt.StreamGAP.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.StreamGAP.entity.Song;
import lombok.Data;
import com.fpt.StreamGAP.entity.User;
import jakarta.persistence.*;

import java.util.Date;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistDTO {
    private Integer playlistId;
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String title;
    private Date created_at;
    private Integer user_id;
}
