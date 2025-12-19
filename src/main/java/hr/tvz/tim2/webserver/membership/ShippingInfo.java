package hr.tvz.tim2.webserver.membership;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInfo {
    private String streetWithNumber;
    private String city;
    private String postalCode;
}
