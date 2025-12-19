package hr.tvz.tim2.webserver.membership;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    @Length(min = 16, max = 16)
    private String cardNumber;
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiration date must be in MM/YY format")
    private String cardExpirationDate;
    @NotBlank(message = "Card holder name cannot be empty!")
    private String cardHolderName;
}
