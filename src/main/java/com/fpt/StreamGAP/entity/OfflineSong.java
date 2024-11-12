package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;

@Entity
@Table(name = "Offline_Songs")
@Data
public class OfflineSong {

    @EmbeddedId
    private OfflineSongId id;

    @ManyToOne
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @MapsId("song_id")
    private Song song;

    private Date downloaded_at;
}
