package com.fpt.StreamGAP.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;

@Data
@Embeddable
public class FavoriteSongId implements Serializable {
    private Integer userId;
    private Integer songId;

    public FavoriteSongId() {
    }

    // Constructor with parameters
    public FavoriteSongId(int userId, int songId) {
        this.userId = userId;
        this.songId = songId;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteSongId)) return false;
        FavoriteSongId that = (FavoriteSongId) o;
        return userId.equals(that.userId) && songId.equals(that.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, songId);
    }
}
