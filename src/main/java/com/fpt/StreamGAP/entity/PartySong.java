package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Party_Songs")
@Data
public class PartySong {

    @EmbeddedId
    private PartySongId id;

    @ManyToOne
    @MapsId("party_id")
    private PartyMode party;

    @ManyToOne
    @MapsId("song_id")
    private Song song;

    private Integer votes = 0;
}
