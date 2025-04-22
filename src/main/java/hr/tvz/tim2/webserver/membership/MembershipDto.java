package hr.tvz.tim2.webserver.membership;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class MembershipDto {
    Instant validUntil;

    CardInfo cardInfo;
    ShippingInfo shippingInfo;
}
