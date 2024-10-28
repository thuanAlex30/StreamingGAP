package com.fpt.StreamGAP.repository;

import com.fpt.StreamGAP.entity.Album;
import com.fpt.StreamGAP.entity.Artist;
import com.fpt.StreamGAP.entity.ChatChannel;
import com.fpt.StreamGAP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatChannelRepository extends JpaRepository<ChatChannel, Integer> {
    List<ChatChannel> findByCreator(User creator);
    List<ChatChannel> findByUsers(User user);

}
