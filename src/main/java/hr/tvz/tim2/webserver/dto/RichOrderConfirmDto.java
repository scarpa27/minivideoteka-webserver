package hr.tvz.tim2.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class RichOrderConfirmDto {
    public RichOrderConfirmDto(OrderConfirmDto order,
                               List<MovieDto> movies) {
        if (order.getItemIdList().size() != movies.size())
            throw new IllegalStateException("Order item id list size mismatch with movies list size!");

        trackingNumber = order.getTrackingNumber();
        orderDate = order.getOrderDate();
        isReturned = order.getIsReturned();
        returnTrackingNumber = order.getReturnTrackingNumber();
        returnDate = order.getReturnDate();

        itemIdList = new HashSet<>();
        order.getItemIdList().forEach(id -> {
            String movieName = movies.stream()
                    .filter(movie -> movie.id.equals(id))
                    .findFirst()
                    .orElseThrow().getTitle();

            itemIdList.add(new ItemDto(id, movieName));
        });
    }


    private String trackingNumber;
    private Instant orderDate;
    private Set<ItemDto> itemIdList;
    private Boolean isReturned;

    private String returnTrackingNumber;
    private Instant returnDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ItemDto {
        private String movieId;
        private String movieName;
    }
}
