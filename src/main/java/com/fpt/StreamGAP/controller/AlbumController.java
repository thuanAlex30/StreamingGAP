package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.AlbumsDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.Album;
import com.fpt.StreamGAP.entity.Playlist;
import com.fpt.StreamGAP.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping
    public ReqRes getAllAlbumsForCurrentUser() {
        List<Album> albums = albumService.getAllPlaylistsForCurrentUser();

        List<AlbumsDTO> albumsDTOS = albums.stream()
                .map(album -> {
                    AlbumsDTO dto = new AlbumsDTO();
                    dto.setAlbum_id(album.getAlbumId());
                    dto.setTitle(album.getTitle());
                    dto.setArtist(album.getArtist());
                    dto.setCover_image_url(album.getCover_image_url());
                    dto.setRelease_date(album.getRelease_date());
                    dto.setCreated_at(album.getCreated_at());

                    return dto;
                })
                .toList();

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Albums retrieved successfully");
        response.setAlbumList(albumsDTOS);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReqRes> getAlbumsByIdForCurrentUser(@PathVariable Integer id) {
        return albumService.getPlaylistsByIdForCurrentUser(id)
                .map(album -> {
                    AlbumsDTO dto = new AlbumsDTO();
                    dto.setAlbum_id(album.getAlbumId());
                    dto.setTitle(album.getTitle());
                    dto.setArtist(album.getArtist());
                    dto.setCover_image_url(album.getCover_image_url());
                    dto.setRelease_date(album.getRelease_date());
                    dto.setCreated_at(album.getCreated_at());

                    ReqRes response = new ReqRes();
                    response.setStatusCode(200);
                    response.setMessage("Album retrieved successfully");
                    response.setAlbumList(List.of(dto));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ReqRes response = new ReqRes();
                    response.setStatusCode(404);
                    response.setMessage("Album not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<ReqRes> createAlbum(@RequestBody Album album) {
        ReqRes response = new ReqRes();
        try {

            album.setCreated_at(new Date());
            Album savedAlbum = albumService.saveAlbums(album);
            AlbumsDTO dto = new AlbumsDTO();
            dto.setAlbum_id(savedAlbum.getAlbumId());
            dto.setArtist(savedAlbum.getArtist());
            dto.setTitle(savedAlbum.getTitle());
            dto.setCover_image_url(savedAlbum.getCover_image_url());
            dto.setRelease_date(savedAlbum.getRelease_date());
            dto.setCreated_at(savedAlbum.getCreated_at());

            response.setStatusCode(201);
            response.setMessage("Album created successfully");
            response.setAlbumList(List.of(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReqRes> updateAlbum(@PathVariable Integer id, @RequestBody Album album) {
        return albumService.getPlaylistsByIdForCurrentUser(id)
                .map(existingAlbum -> {
                    album.setAlbumId(id);

                    album.setCreated_at(new Date());

                    Album updatedAlbum = albumService.saveAlbums(album);
                    AlbumsDTO dto = new AlbumsDTO();
                    dto.setAlbum_id(updatedAlbum.getAlbumId());
                    dto.setTitle(updatedAlbum.getTitle());
                    dto.setArtist(updatedAlbum.getArtist());
                    dto.setCover_image_url(updatedAlbum.getCover_image_url());
                    dto.setRelease_date(updatedAlbum.getRelease_date());
                    dto.setCreated_at(updatedAlbum.getCreated_at());

                    ReqRes response = new ReqRes();
                    response.setStatusCode(200);
                    response.setMessage("Album updated successfully");
                    response.setAlbumList(List.of(dto));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ReqRes response = new ReqRes();
                    response.setStatusCode(404);
                    response.setMessage("Album not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReqRes> deleteAlbumsForCurrentUser(@PathVariable Integer id) {
        ReqRes response = new ReqRes();
        if (albumService.getPlaylistsByIdForCurrentUser(id).isPresent()) {
            albumService.deleteAlbumsForCurrentUser(id);
            response.setStatusCode(200);
            response.setMessage("Album deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(404);
            response.setMessage("Album not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}


