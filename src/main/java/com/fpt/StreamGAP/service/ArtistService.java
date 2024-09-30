package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.Artist;
import com.fpt.StreamGAP.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Artist getArtistById(Integer id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isPresent()) {
            return artist.get();
        } else {
            throw new RuntimeException("Không tìm thấy nghệ sĩ với ID: " + id);
        }
    }

}