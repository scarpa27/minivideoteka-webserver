package hr.tvz.tim2.webserver.membership;

import hr.tvz.tim2.webserver.security.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MemberEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    private String shippingAddress;

    private Instant validUntil;

    private BigDecimal creditsLeft;

}