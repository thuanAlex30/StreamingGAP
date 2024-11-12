package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.CommentDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<ReqRes> getAllComments() {
        List<CommentDTO> comments = commentService.getAllComments();
        ReqRes response = new ReqRes();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Comments retrieved successfully.");
        response.setCommentList(comments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<ReqRes> createComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO createdComment = commentService.createComment(commentDTO);

        ReqRes response = new ReqRes();
        if (createdComment != null) {
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage("Comment created successfully.");
            response.setCommentList(List.of(createdComment));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Failed to create comment.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ReqRes> updateComment(@PathVariable Integer commentId, @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO);
        ReqRes response = new ReqRes();
        if (updatedComment != null) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Comment updated successfully.");
            response.setCommentList(List.of(updatedComment));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("You do not own this Comment or Comment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ReqRes> deleteComment(@PathVariable Integer commentId) {
        boolean isDeleted = commentService.deleteComment(commentId);
        ReqRes response = new ReqRes();
        if (isDeleted) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Comment deleted successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("You do not own this Comment or Comment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ReqRes> getCommentById(@PathVariable Integer commentId) {
        Optional<CommentDTO> comment = commentService.getCommentById(commentId);
        ReqRes response = new ReqRes();
        if (comment.isPresent()) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Comment retrieved successfully.");
            response.setCommentList(List.of(comment.get()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("You do not own this Comment or Comment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
