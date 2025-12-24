package hr.tvz.tim2.webserver.stock;

import hr.tvz.tim2.webserver.movie.entities.MovieEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockEntity {
    @Id
    private String id;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "movie_id", unique = true)
    private MovieEntity movie;

    private int quantity;
}