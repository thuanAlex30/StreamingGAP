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
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private UserRepo userRepo;


    private String getCurrentUsername() {
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
        Optional<Album> existingAlbumOpt = albumRepository.findByTitleAndUser(album.getTitle(), currentUser);
        if (existingAlbumOpt.isPresent()) {
            Album existingAlbum = existingAlbumOpt.get();
            if (album.getAlbumId() == null || !existingAlbum.getAlbumId().equals(album.getAlbumId())) {
                throw new IllegalStateException("A album with the same title already exists for this user.");
            }
        }
        if (isAdmin()) {
            Optional<Album> adminExistingAlbumOpt = albumRepository.findByTitleAndUser(album.getTitle(), album.getUser());
            if (adminExistingAlbumOpt.isPresent() && !adminExistingAlbumOpt.get().getAlbumId().equals(album.getAlbumId())) {
                throw new IllegalStateException("A playlist with the same title already exists for this user.");
            }
        } else {
            if (album.getUser() == null || !album.getUser().getUser_id().equals(currentUser.getUser_id())) {
                album.setUser(currentUser);
            } else {
                throw new IllegalStateException("You can only edit your own playlist.");
            }
        }
        return albumRepository.save(album);
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

