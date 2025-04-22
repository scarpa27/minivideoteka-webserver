package hr.tvz.tim2.webserver.membership;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCommand {
    String cardNumber;
    String cardExpirationDate;
    String cardHolderName;

    String streetWithNumber;
    String city;
    String postalCode;
}
