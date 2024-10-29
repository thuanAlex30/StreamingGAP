package com.fpt.StreamGAP.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.StreamGAP.entity.Artist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class AlbumsDTO {
    private Artist artist;
    private Integer album_id;
    private String title;
    private Date release_date;
    private String cover_image_url;
    private Date created_at;

}
