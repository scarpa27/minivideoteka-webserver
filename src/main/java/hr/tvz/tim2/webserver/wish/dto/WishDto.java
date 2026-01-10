package hr.tvz.tim2.webserver.wish.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishDto {
    private String imdbId;
    private String message;
    private boolean isFulfilled;
}
