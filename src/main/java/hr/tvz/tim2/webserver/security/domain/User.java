package hr.tvz.tim2.webserver.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.tvz.tim2.webserver.basket.BasketEntity;
import hr.tvz.tim2.webserver.membership.MemberEntity;
import hr.tvz.tim2.webserver.ordering.entities.OrderEntity;
import hr.tvz.tim2.webserver.review.ReviewEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    @Column(length = 1024)
    private String password;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "USER_AUTHORITY", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    @BatchSize(size = 20)
    private Set<Authority> authorities = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderEntity> orders = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BasketEntity> baskets = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReviewEntity> reviews = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private MemberEntity membership = null;

    public User(String user,
                String pass) {
        this.username = user;
        this.password = pass;
    }

    public void addAuthority(Authority nAuth) {
        authorities.add(nAuth);
    }
}