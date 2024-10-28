package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.entity.Playlist;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.PlaylistRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserManagementService userManagementService;

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
    public List<Playlist> getAllPlaylistsForCurrentUser() {
        if (isAdmin()) {
            return playlistRepository.findAll();
        } else {
            String currentUsername = getCurrentUsername();
            return playlistRepository.findByUserUsername(currentUsername);
        }
    }

    public Optional<Playlist> getPlaylistsByIdForCurrentUser(Integer id) {
        if (isAdmin()) {
            return playlistRepository.findById(id);
        } else {
            String currentUsername = getCurrentUsername();
            return playlistRepository.findByPlaylistIdAndUserUsername(id, currentUsername);
        }
    }
    public Playlist savePlaylist(Playlist playlist) {
        String currentUsername = getCurrentUsername();
        User currentUser = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Playlist> existingPlaylistOpt = playlistRepository.findByTitleAndUser(playlist.getTitle(), currentUser);
        if (existingPlaylistOpt.isPresent()) {
            Playlist existingPlaylist = existingPlaylistOpt.get();
            if (playlist.getPlaylistId() == null || !existingPlaylist.getPlaylistId().equals(playlist.getPlaylistId())) {
                throw new IllegalStateException("A playlist with the same title already exists for this user.");
            }
        }
        if (isAdmin()) {
            Optional<Playlist> adminExistingPlaylistOpt = playlistRepository.findByTitleAndUser(playlist.getTitle(), playlist.getUser());
            if (adminExistingPlaylistOpt.isPresent() && !adminExistingPlaylistOpt.get().getPlaylistId().equals(playlist.getPlaylistId())) {
                throw new IllegalStateException("A playlist with the same title already exists for this user.");
            }
        } else {
            if (playlist.getUser() == null || !playlist.getUser().getUser_id().equals(currentUser.getUser_id())) {
                throw new IllegalStateException("You can only edit your own playlist.");
            }
            playlist.setUser(currentUser);
        }

        return playlistRepository.save(playlist);
    }


    public void deletePlaylistsForCurrentUser(Integer id) {
        if (isAdmin()) {
            playlistRepository.deleteById(id);
        } else {
            String currentUsername = getCurrentUsername();
            Optional<Playlist> partyMode = playlistRepository.findByPlaylistIdAndUserUsername(id, currentUsername);
            partyMode.ifPresent(playlistRepository::delete);
        }
    }
}