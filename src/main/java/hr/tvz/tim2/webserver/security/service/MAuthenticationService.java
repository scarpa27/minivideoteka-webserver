package hr.tvz.tim2.webserver.security.service;

import hr.tvz.tim2.webserver.security.command.LoginCommand;
import hr.tvz.tim2.webserver.security.domain.Authority;
import hr.tvz.tim2.webserver.security.domain.User;
import hr.tvz.tim2.webserver.security.dto.LoginDTO;
import hr.tvz.tim2.webserver.security.repository.AuthorityRepository;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MAuthenticationService implements AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthorityRepository authRepository;

    public MAuthenticationService(@Autowired JwtService jwtService, @Autowired UserRepository userRepository, @Autowired AuthorityRepository authRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    @Override
    public Optional<LoginDTO> login(LoginCommand command) {
        Optional<User> user = userRepository.findByUsername(command.getUsername());

        if (user.isEmpty() || !isMatchingPassword(command.getPassword(), user.get().getPassword())) {
            return Optional.empty();
        }

        String token = jwtService.createJwt(user.get());

        return Optional.of(new LoginDTO(token, jwtService.getAuthorities(token)));
    }

    @Override
    public boolean register(LoginCommand command) {
        setupAuthorities();
        User user = new User(command.getUsername(), encodedPassword(command.getPassword()));
        Authority basicAuth = authRepository.findByName("ROLE_USER").orElseThrow();
        user.addAuthority(basicAuth);
        user = userRepository.save(user);
        return userRepository.exists(Example.of(user));
    }

    @Override
    public boolean userExists(LoginCommand command) {
        return userRepository.findByUsername(command.getUsername()).isPresent();
    }

    private boolean isMatchingPassword(String rawPassword, String encryptedPassword) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(rawPassword, 128, 10, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1);
        return encoder.matches(rawPassword, encryptedPassword);
    }

    private String encodedPassword(String raw) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(raw, 128, 10, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1);
        return encoder.encode(raw);
    }

    private void setupAuthorities() {
        authRepository.findByName("ROLE_ADMIN").ifPresentOrElse(x -> {
        }, () -> {
            var admin = new Authority();
            admin.setName("ROLE_ADMIN");
            authRepository.save(admin);
        });

        authRepository.findByName("ROLE_USER").ifPresentOrElse(x -> {
        }, () -> {
            var user = new Authority();
            user.setName("ROLE_USER");
            authRepository.save(user);
        });

        authRepository.flush();
    }
}