package hr.tvz.tim2.webserver.basket.logic;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

@Getter
public class BasketItem {

    private Long userBasketId;
    private final String itemId;
    private Long basketItemId;

    private final Date reservedUntil;

    public BasketItem(String itemId) {
        this.itemId = itemId;

        reservedUntil = Date.from(Instant.now().plus(15, ChronoUnit.MINUTES));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BasketItem that)) return false;
        return Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemId);
    }
}
