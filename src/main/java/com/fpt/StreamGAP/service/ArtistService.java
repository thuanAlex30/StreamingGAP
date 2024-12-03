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

    public Optional<Artist> getArtistById(Integer id) {
        return artistRepository.findById(id);
    }

    public Artist saveArtist(Artist artist) {
        if (artist.getArtist_id() != null) {
            Optional<Artist> existingArtist = artistRepository.findById(artist.getArtist_id());
            if (existingArtist.isPresent()) {
                Artist updatedArtist = existingArtist.get();
                if (artist.getName() != null) {
                    updatedArtist.setName(artist.getName());
                }
                if (artist.getBio() != null) {
                    updatedArtist.setBio(artist.getBio());
                }
                if (artist.getProfile_image_url() != null) {
                    updatedArtist.setProfile_image_url(artist.getProfile_image_url());
                }
                if (artist.getCreated_at() != null) {
                    updatedArtist.setCreated_at(artist.getCreated_at()); // Cập nhật nếu có
                }
                return artistRepository.save(updatedArtist);
            }
        }
        return artistRepository.save(artist);
    }


    public void deleteArtist(Integer id) {
        artistRepository.deleteById(id);
    }
}