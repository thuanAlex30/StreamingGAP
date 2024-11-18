package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.*;
import com.fpt.StreamGAP.repository.AlbumRepository;
import com.fpt.StreamGAP.repository.ArtistRepository;
import com.fpt.StreamGAP.repository.PlaylistSongRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ArtistRepository artistRepository;

    public List<Album> getAllAlbum() {
        return albumRepository.findAll();
    }
    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    private boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Collection<? extends GrantedAuthority> authorities = ((UserDetails) principal).getAuthorities();
            return authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        }
        return false;
    }
    public List<Album> getAllPlaylistsForCurrentUser() {
        if (isAdmin()) {
            return albumRepository.findAll();
        } else {
            String currentUsername = getCurrentUsername();
            return albumRepository.findByUserUsername(currentUsername);
        }
    }

    public Optional<Album> getPlaylistsByIdForCurrentUser(Integer id) {
        if (isAdmin()) {
            return albumRepository.findById(id);
        } else {
            String currentUsername = getCurrentUsername();
            return albumRepository.findByAlbumIdAndUserUsername(id, currentUsername);
        }
    }

    public Album saveAlbums(Album album) {
        String currentUsername = getCurrentUsername();
        User currentUser = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (album.getUser() == null) {
            album.setUser(currentUser);
        } else if (!album.getUser().getUser_id().equals(currentUser.getUser_id())) {
            throw new IllegalStateException("You can only edit your own album.");
        }
        if (album.getArtist() != null) {
            Artist artist = album.getArtist();
            if (artist.getArtist_id() != null) {
                Optional<Artist> artistOpt = artistRepository.findById(artist.getArtist_id());
                Artist foundArtist = artistOpt.orElseThrow(() -> new RuntimeException("Artist with id " + artist.getArtist_id() + " not found"));
                if (artist.getName() != null) foundArtist.setName(artist.getName());
                if (artist.getBio() != null) foundArtist.setBio(artist.getBio());
                if (artist.getProfile_image_url() != null) foundArtist.setProfile_image_url(artist.getProfile_image_url());
                if (artist.getCreated_at() != null) foundArtist.setCreated_at(artist.getCreated_at());

                album.setArtist(foundArtist);
            } else {
                throw new IllegalStateException("Artist information is required. You must provide an artist_id or a complete artist object.");
            }
        }
        if (album.getAlbumId() == null) {
            return albumRepository.save(album);
        } else {
            Optional<Album> existingAlbumOpt = albumRepository.findById(album.getAlbumId());
            if (existingAlbumOpt.isPresent()) {
                Album existingAlbum = existingAlbumOpt.get();
                if (album.getTitle() != null) {
                    existingAlbum.setTitle(album.getTitle());
                }
                if (album.getRelease_date() != null) {
                    existingAlbum.setRelease_date(album.getRelease_date());
                }
                if (album.getCover_image_url() != null) {
                    existingAlbum.setCover_image_url(album.getCover_image_url());
                }
                if (album.getCreated_at() != null) {
                    existingAlbum.setCreated_at(album.getCreated_at());
                }
                return albumRepository.save(existingAlbum);
            } else {
                throw new RuntimeException("Album not found with id " + album.getAlbumId());
            }
        }
    }



    public void deleteAlbumsForCurrentUser(Integer id) {
        if (isAdmin()) {
            albumRepository.deleteById(id);
        } else {
            String currentUsername = getCurrentUsername();
            Optional<Album> partyMode = albumRepository.findByAlbumIdAndUserUsername(id, currentUsername);
            partyMode.ifPresent(albumRepository::delete);
        }
    }

}