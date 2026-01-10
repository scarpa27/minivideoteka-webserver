package hr.tvz.tim2.webserver.wish.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WishCountDto {
    private String imdbId;
    private Long wishCount;
    private boolean isFulfilled;
}
