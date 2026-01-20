package hr.tvz.tim2.webserver.admin;

import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.UserDto;
import hr.tvz.tim2.webserver.security.domain.User;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
        log.debug("AdminService created");
    }

    public void banUser(Long userId) {
        log.debug("Banning user with id: {}", userId);
        userRepository.updateBannedById(userId, true);
    }

    public void unbanUser(Long userId) {
        log.debug("Unbanning user with id: {}", userId);
        userRepository.updateBannedById(userId, false);
    }

    public void banUser(String username) {
        log.debug("Banning user with username: {}", username);
        userRepository.updateBannedByUsername(username, true);
    }

    public void unbanUser(String username) {
        log.debug("Unbanning user with username: {}", username);
        userRepository.updateBannedByUsername(username, false);
    }

    public List<UserDto> getAllUsers() {
        log.debug("Getting all users");
        List<User> entities = userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"));
        return entities.stream().map(DtoMapper::toDto).toList();
    }
}
