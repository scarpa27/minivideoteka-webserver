package hr.tvz.tim2.webserver.admin;

import hr.tvz.tim2.webserver.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void banUser(Long userId) {
        userRepository.updateBannedById(userId, true);
    }

    public void unbanUser(Long userId) {
        userRepository.updateBannedById(userId, false);
    }

    public void banUser(String username) {
        userRepository.updateBannedByUsername(username, true);
    }

    public void unbanUser(String username) {
        userRepository.updateBannedByUsername(username, false);
    }
}
