package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Favorite_Songs")
@Data
public class FavoriteSong {

    @EmbeddedId
    private FavoriteSongId f_id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("songId")
    @JoinColumn(name = "song_id", referencedColumnName = "song_id", insertable = false, updatable = false)
    private Song song;

    private Date markedAt;
    public FavoriteSong(int userId, int songId) {
        this.f_id = new FavoriteSongId(userId, songId);
        this.markedAt = new Date();
    }
    public FavoriteSong(FavoriteSongId favoriteSongId) {
        this.f_id = favoriteSongId;
        this.markedAt = new Date();
    }

    public FavoriteSong() {
    }
}
