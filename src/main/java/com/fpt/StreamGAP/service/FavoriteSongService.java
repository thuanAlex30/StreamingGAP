package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.FavoriteSongDTO;
import com.fpt.StreamGAP.entity.FavoriteSong;
import com.fpt.StreamGAP.entity.FavoriteSongId;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.service.JWTUtils;
import com.fpt.StreamGAP.repository.FavoriteSongRepository;
import com.fpt.StreamGAP.repository.SongRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteSongService {

    @Autowired
    private FavoriteSongRepository favoriteSongRepository;
    @Autowired
    private UserRepo userRe;
    @Autowired
    private SongRepository songRepo;

    public List<FavoriteSong> getAllFavoriteSongsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRe.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return favoriteSongRepository.findByUser(currentUser);
    }


    public Optional<FavoriteSong> getFavoriteSongById(FavoriteSongId id) {
        return favoriteSongRepository.findById(id);
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
    public User getUserByUsername(String username) {
        return userRe.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public Song getSongById(int songId) {
        return songRepo.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
    }
    @Transactional
    public FavoriteSong createFavori(FavoriteSongDTO favoriteSongDTO) {
        try {
            String currentUsername = getCurrentUsername();
            if (currentUsername == null) {
                throw new RuntimeException("User not authenticated");
            }

            User currentUser = getUserByUsername(currentUsername);
            int userId = currentUser.getUser_id();

            Song song = getSongById(favoriteSongDTO.getSongId());
            if (song == null) {
                throw new RuntimeException("Song not found");
            }

            FavoriteSong favoriteSong = new FavoriteSong(new FavoriteSongId(userId, favoriteSongDTO.getSongId()));
            favoriteSong.setSong(song);
            favoriteSong.setUser(currentUser);
            favoriteSong.setMarkedAt(new Date());
            return favoriteSongRepository.save(favoriteSong);
        } catch (Exception e) {
            throw new RuntimeException("Error saving favorite song: " + e.getMessage(), e);
        }
    }

    public void deleteFavoriteSong(int userId, int songId) {
        FavoriteSongId favoriteSongId = new FavoriteSongId(userId, songId);
        if (favoriteSongRepository.existsById(favoriteSongId)) {
            favoriteSongRepository.deleteById(favoriteSongId);
        } else {
            throw new RuntimeException("Favorite song not found for userId: " + userId + " and songId: " + songId);
        }
    }
}
