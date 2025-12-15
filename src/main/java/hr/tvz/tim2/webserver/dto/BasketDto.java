package hr.tvz.tim2.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class BasketDto {
    private Instant validUntil;
    private Set<ItemDto> items;

    @Data
    @AllArgsConstructor
    static class ItemDto {
        private String movieId;
        private Instant reservedUntil;
    }
}
