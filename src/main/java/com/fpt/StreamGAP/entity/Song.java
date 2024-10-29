package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Songs")
@Data
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id", nullable = false)
    private Integer songId;
    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;
    private String title;
    private String genre;
    private Integer duration;
    private String audio_file_url;
    private String lyrics;
    private Date created_at;
    private Integer Listen_count=0;
    @Column(name = "created_by_username")
    private String createdByUsername;
}
