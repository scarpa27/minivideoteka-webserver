package hr.tvz.tim2.webserver.wish;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class WishEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String imdbId;

    private String username;

    private String message;

    boolean fulfilled;
}
