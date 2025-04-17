package hr.tvz.tim2.webserver.stock.logic;

import hr.tvz.tim2.webserver.domain.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StockEntity {
    @Id
    private String id;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "movie_id", unique = true)
    private Movie movie;

    private int quantity;
}