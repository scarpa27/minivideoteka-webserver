package hr.tvz.tim2.webserver.ordering;

import hr.tvz.tim2.webserver.security.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
public class OrderEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_o_id")
    public User user = null;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<OrderItemEntity> itemIdList;

    public Instant orderDate;

    public Instant returnDate;

    public Boolean isReturned;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
}
