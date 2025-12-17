package hr.tvz.tim2.webserver.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class OrderConfirmDto {
    private String trackingNumber;
    private Instant orderDate;
    private Set<String> itemIdList;

    private Boolean isReturned;


}
