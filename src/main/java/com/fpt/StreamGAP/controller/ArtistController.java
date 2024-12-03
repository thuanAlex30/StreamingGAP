package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.ArtistDTO;
import com.fpt.StreamGAP.dto.ReqRes;
import com.fpt.StreamGAP.entity.Artist;
import com.fpt.StreamGAP.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    // Phương thức kiểm tra vai trò admin
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }

    @GetMapping
    public ReqRes getAllArtists() {
        List<Artist> artists = artistService.getAllArtists();

        List<ArtistDTO> artistDTOs = artists.stream()
                .map(artist -> {
                    ArtistDTO dto = new ArtistDTO();
                    dto.setArtist_id(artist.getArtist_id());
                    dto.setName(artist.getName());
                    dto.setBio(artist.getBio());
                    dto.setCreated_at(artist.getCreated_at());
                    dto.setProfile_image_url(artist.getProfile_image_url());
                    return dto;
                })
                .collect(Collectors.toList());

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Artists retrieved successfully");
        response.setArtistList(artistDTOs);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReqRes> getArtistById(@PathVariable Integer id) {
        return artistService.getArtistById(id)
                .map(artist -> {
                    ArtistDTO dto = new ArtistDTO();
                    dto.setArtist_id(artist.getArtist_id());
                    dto.setName(artist.getName());
                    dto.setBio(artist.getBio());
                    dto.setCreated_at(artist.getCreated_at());
                    dto.setProfile_image_url(artist.getProfile_image_url());

                    ReqRes response = new ReqRes();
                    response.setStatusCode(200);
                    response.setMessage("Artist retrieved successfully");
                    response.setArtistList(List.of(dto));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ReqRes response = new ReqRes();
                    response.setStatusCode(404);
                    response.setMessage("Artist not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<ReqRes> createArtist(@RequestBody Artist artist) {
        if (!isAdmin()) {
            ReqRes response = new ReqRes();
            response.setStatusCode(403);
            response.setMessage("Only admins can create artists");
            return ResponseEntity.status(403).body(response);
        }

        // Thiết lập thời gian tạo thành thời gian hiện tại bằng java.util.Date
        artist.setCreated_at(new Date());

        Artist savedArtist = artistService.saveArtist(artist);

        ArtistDTO dto = new ArtistDTO();
        dto.setArtist_id(savedArtist.getArtist_id());
        dto.setName(savedArtist.getName());
        dto.setBio(savedArtist.getBio());
        dto.setCreated_at(savedArtist.getCreated_at());
        dto.setProfile_image_url(artist.getProfile_image_url());

        ReqRes response = new ReqRes();
        response.setStatusCode(201);
        response.setMessage("Artist created successfully");
        response.setArtistList(List.of(dto));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReqRes> updateArtist(@PathVariable Integer id, @RequestBody Artist updatedArtistData) {
        if (!isAdmin()) {
            ReqRes response = new ReqRes();
            response.setStatusCode(403);
            response.setMessage("Only admins can update artists");
            return ResponseEntity.status(403).body(response);
        }

        return artistService.getArtistById(id)
                .map(existingArtist -> {
                    if (updatedArtistData.getName() != null && !updatedArtistData.getName().equals(existingArtist.getName())) {
                        existingArtist.setName(updatedArtistData.getName());
                    }
                    if (updatedArtistData.getBio() != null && !updatedArtistData.getBio().equals(existingArtist.getBio())) {
                        existingArtist.setBio(updatedArtistData.getBio());
                    }
                    if (updatedArtistData.getCreated_at() != null && !updatedArtistData.getCreated_at().equals(existingArtist.getCreated_at())) {
                        existingArtist.setCreated_at(updatedArtistData.getCreated_at());
                    }

                    Artist updatedArtist = artistService.saveArtist(existingArtist);

                    ArtistDTO dto = new ArtistDTO();
                    dto.setArtist_id(updatedArtist.getArtist_id());
                    dto.setName(updatedArtist.getName());
                    dto.setBio(updatedArtist.getBio());
                    dto.setCreated_at(updatedArtist.getCreated_at());
                    dto.setProfile_image_url(updatedArtist.getProfile_image_url());

                    ReqRes response = new ReqRes();
                    response.setStatusCode(200);
                    response.setMessage("Artist updated successfully");
                    response.setArtistList(List.of(dto));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ReqRes response = new ReqRes();
                    response.setStatusCode(404);
                    response.setMessage("Artist not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReqRes> deleteArtist(@PathVariable Integer id) {
        if (!isAdmin()) {
            ReqRes response = new ReqRes();
            response.setStatusCode(403);
            response.setMessage("Only admins can delete artists");
            return ResponseEntity.status(403).body(response);
        }

        if (artistService.getArtistById(id).isPresent()) {
            artistService.deleteArtist(id);
            ReqRes response = new ReqRes();
            response.setStatusCode(204);
            response.setMessage("Artist deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            ReqRes response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("Artist not found");
            return ResponseEntity.status(404).body(response);
        }
    }

}
