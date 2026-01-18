package hr.tvz.tim2.webserver.movie.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class YoutubeTrailerEntity {
    @Id
    private String videoId;
    private String provider;
    private String name;
    private String watchUrl;
    private String embedUrl;

    @JsonIgnore
    @OneToOne(mappedBy = "youtubeTrailer")
    private MovieEntity movie;
}
