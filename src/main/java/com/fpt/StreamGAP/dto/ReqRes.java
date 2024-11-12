package com.fpt.StreamGAP.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.StreamGAP.entity.*;
import lombok.Data;
import lombok.Getter;

import java.sql.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String username;
    private String email;
    private String code;
    private String avatar_url;
    private String login_provider;
    private String provider_id;
    private String subscription_type;
    private Date created_at;
    private Date updated_at;
    private String role;
    private String password;
    private User user;
    private List<User> userList;
    private List<Song> songList;
    private List<SongDTO> songDtoList;
    private Integer albumId;
    private List<AccountSettingsDTO> accountSettingsList;
    private List<FavoriteSongDTO.PlaylistSongDTO> playlistSongList;
    private List<PartySongDTO> partySongList;
    private List<PartyModeDTO> partyModeList;
    private List<MusicGameDTO> musicGameList;
    private List<KaraokeSessionDTO> karaokeSessionList;
    private List<FavoriteSongDTO> favoriteSongList;
    private List<AlbumsDTO> albumList;
    private List<CommentDTO> commentList;
    private List<PlaylistDTO> playList;
    private List<ArtistDTO> artistList;
    private List<SongListenStatsDTO> songListenStatsList;
    private List<MessageDTO> messages;//

    @Getter
    private Song song;

    public void setPlaylistSongList(List<FavoriteSongDTO.PlaylistSongDTO> playlistSongList) {
        this.playlistSongList = playlistSongList;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public void setKaraokeSessionList(List<KaraokeSessionDTO> karaokeSessionList) {
        this.karaokeSessionList = karaokeSessionList;
    }

    public void setFavoriteSongList(List<FavoriteSongDTO> favoriteSongList) {
        this.favoriteSongList = favoriteSongList;
    }

    public void setSongDtoList(List<SongDTO> songDtoList) {
        this.songDtoList = songDtoList;
    }

}
