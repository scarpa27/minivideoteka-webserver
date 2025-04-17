package hr.tvz.tim2.webserver.ordering;

import hr.tvz.tim2.webserver.basket.logic.BasketItemEntity;
import hr.tvz.tim2.webserver.security.domain.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Entity
@NoArgsConstructor
public class OrderEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_o_id")
    public User user = null;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<BasketItemEntity> itemIdList;

    public Instant orderDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

}
