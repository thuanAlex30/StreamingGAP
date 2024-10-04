package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.Album;
import com.fpt.StreamGAP.entity.Artist;
import com.fpt.StreamGAP.entity.PlaylistSong;
import com.fpt.StreamGAP.repository.AlbumRepository;
import com.fpt.StreamGAP.repository.ArtistRepository;
import com.fpt.StreamGAP.repository.PlaylistSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public Optional<Album> getAlbumById(Integer id) {
        return albumRepository.findById(id);
    }

    public Album saveAlbum(Album album) {
        return albumRepository.save(album);
    }

    public void deleteAlbum(Integer id) {
        albumRepository.deleteById(id);
    }
}