package hr.tvz.tim2.webserver.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Company extends Creator {
    public Company(String id) {
        super.setId(id);
    }
}
