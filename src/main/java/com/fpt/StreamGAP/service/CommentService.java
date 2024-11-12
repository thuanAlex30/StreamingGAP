package com.fpt.StreamGAP.service;

import com.fpt.StreamGAP.dto.CommentDTO;
import com.fpt.StreamGAP.entity.Comment;
import com.fpt.StreamGAP.entity.Song;
import com.fpt.StreamGAP.entity.User;
import com.fpt.StreamGAP.repository.CommentRepository;
import com.fpt.StreamGAP.repository.SongRepository;
import com.fpt.StreamGAP.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepo userRepository;

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }


    public List<CommentDTO> getAllComments() {
        String currentUsername = getCurrentUsername();
        User user = userRepository.findByUsername(currentUsername).orElse(null);

        if (user == null) {
            return List.of();
        }

        if (user.getRole().equals("ADMIN")) {
            List<Comment> comments = commentRepository.findAll();
            return comments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            List<Comment> comments = commentRepository.findByUser_Username(currentUsername);
            return comments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }


    public CommentDTO createComment(CommentDTO commentDTO) {
        User user = userRepository.findByUsername(getCurrentUsername()).orElse(null);
        Song song = songRepository.findById(commentDTO.getSongId()).orElse(null);

        if (user == null || song == null) {
            return null;  // Xử lý ngoại lệ
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setSong(song);
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(new Date(System.currentTimeMillis()));

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public CommentDTO updateComment(Integer commentId, CommentDTO commentDTO) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            if (!comment.getUser().getUsername().equals(getCurrentUsername())) {
                return null; // Người dùng không sở hữu comment này
            }
            comment.setContent(commentDTO.getContent());
            comment.setCreatedAt(new Date(System.currentTimeMillis()));
            comment = commentRepository.save(comment);
            return convertToDTO(comment);
        }
        return null;
    }

    public boolean deleteComment(Integer commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent() && commentOptional.get().getUser().getUsername().equals(getCurrentUsername())) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false; // Comment không tồn tại hoặc không thuộc về người dùng
    }

    public Optional<CommentDTO> getCommentById(Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent() && comment.get().getUser().getUsername().equals(getCurrentUsername())) {
            return comment.map(this::convertToDTO);
        }
        return Optional.empty(); // Comment không tồn tại hoặc không thuộc về người dùng
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getComment_id());
        dto.setUserId(comment.getUser().getUser_id());
        dto.setSongId(comment.getSong().getSongId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
