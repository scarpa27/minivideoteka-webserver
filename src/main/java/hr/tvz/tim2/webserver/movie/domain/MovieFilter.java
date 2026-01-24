package hr.tvz.tim2.webserver.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieFilter {
    private BigDecimal ratingMin;
    private BigDecimal ratingMax;
    private Integer yearFrom;
    private Integer yearTo;
    private String q;
}
