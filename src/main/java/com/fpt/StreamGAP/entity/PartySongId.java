package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class PartySongId implements Serializable {
    private Integer party_id;
    private Integer song_id;
    // Default constructor
    public PartySongId() {}

    // Constructor with parameters
    public PartySongId(Integer party_id, Integer song_id) {
        this.party_id = party_id;
        this.song_id = song_id;
    }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartySongId)) return false;
        PartySongId that = (PartySongId) o;
        return Objects.equals(party_id, that.party_id) &&
                Objects.equals(song_id, that.song_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party_id, song_id);
    }

}
