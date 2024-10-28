package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.Album;
import com.fpt.StreamGAP.entity.Playlist;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByUserUsername(String username);
    Optional<Album> findByAlbumIdAndUserUsername(Integer albumId, String username);
    Optional<Album> findByTitleAndUser( String title, User user);
}
