package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Playlists")
@Data
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Integer playlistId;
    @OneToMany(mappedBy = "playlist")
    private List<PlaylistSong> songs;
    private String playlist;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String title;
    private Date created_at;
}
