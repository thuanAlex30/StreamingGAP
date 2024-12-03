package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Entity
@Table(name = "Karaoke_Sessions")
@Data
public class KaraokeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer session_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    private String recordingUrl;
    private Date created_at;
}
