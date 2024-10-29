package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class OfflineSongId implements Serializable {
    private Integer user_id;
    private Integer song_id;
}
