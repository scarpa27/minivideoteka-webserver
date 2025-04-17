package hr.tvz.tim2.webserver.security.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ApplicationUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 8533039291044343363L;

    private String username;
    private String password;
    private List<SimpleGrantedAuthority> authorities;

}
