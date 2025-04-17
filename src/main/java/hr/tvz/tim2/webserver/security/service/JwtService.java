package hr.tvz.tim2.webserver.security.service;

import hr.tvz.tim2.webserver.security.domain.User;

import java.util.List;

public interface JwtService {

    boolean authenticate(String token);

    String createJwt(User user);

    List<String> getAuthorities(String token);
}
