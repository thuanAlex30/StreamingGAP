package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;

@Entity
@Table(name = "Playlist_Songs")
@Data
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "song_id")
    private Song song;
    private Date added_at;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;


    public Date getAdded_at() {
        return added_at;
    }

    public void setAdded_at(Date added_at) {
        this.added_at = added_at;
    }
}