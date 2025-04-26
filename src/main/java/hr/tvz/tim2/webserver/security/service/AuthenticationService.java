package hr.tvz.tim2.webserver.security.service;

import hr.tvz.tim2.webserver.security.command.LoginCommand;
import hr.tvz.tim2.webserver.security.dto.LoginDTO;

import java.util.Optional;

public interface AuthenticationService {

    Optional<LoginDTO> login(LoginCommand command);

    boolean userExists(LoginCommand command);

    boolean register(LoginCommand command);

    boolean registerAdmin(LoginCommand command);
}
