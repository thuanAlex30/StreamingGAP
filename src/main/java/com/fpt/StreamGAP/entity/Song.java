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
    private Integer song_id;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private String title;
    private String genre;
    private Integer duration;
    private String audio_file_url;
    private String lyrics;
    private Date created_at;
    private Integer Listen_count;
    public Integer getSong_id() {
        return song_id;
    }

    public void setSong_id(Integer song_id) {
        this.song_id = song_id;
    }

    // Getter và Setter cho album
    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    // Getter và Setter cho title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho genre
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // Getter và Setter cho duration
    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    // Getter và Setter cho audio_file_url
    public String getAudio_file_url() {
        return audio_file_url;
    }

    public void setAudio_file_url(String audio_file_url) {
        this.audio_file_url = audio_file_url;
    }

    // Getter và Setter cho lyrics
    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

}
