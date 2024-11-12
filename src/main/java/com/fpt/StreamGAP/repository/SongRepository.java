package com.fpt.StreamGAP.repository;


import com.fpt.StreamGAP.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Song> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(@Param("keyword") String keyword);
}