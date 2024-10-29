package com.fpt.StreamGAP.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;


@Entity
@Table(name = "Artists")
@Data
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer artist_id;
    private String name;
    private String bio;
    private String profile_image_url;
    private Date created_at;
}