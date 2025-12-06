package hr.tvz.tim2.webserver.security.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Set;

@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "authority_name", length = 50, nullable = false)
    @Getter
    private String name;
    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
