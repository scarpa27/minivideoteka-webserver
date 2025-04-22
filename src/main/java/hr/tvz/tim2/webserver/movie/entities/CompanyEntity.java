package hr.tvz.tim2.webserver.movie.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CompanyEntity extends CreatorEntity {
    public CompanyEntity(String id) {
        super.setId(id);
    }
}
