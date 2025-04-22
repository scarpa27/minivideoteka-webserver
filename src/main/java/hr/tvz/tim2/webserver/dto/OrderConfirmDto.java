package hr.tvz.tim2.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmDto {
    private String trackingNumber;
    private Instant orderDate;
    private Set<String> itemIdList;

    private Boolean isReturned;

    private String returnTrackingNumber;
    private Instant returnDate;
}
