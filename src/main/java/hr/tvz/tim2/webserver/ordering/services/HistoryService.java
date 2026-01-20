package hr.tvz.tim2.webserver.ordering.services;

import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.ordering.entities.OrderEntity;
import hr.tvz.tim2.webserver.ordering.repositories.OrderDbRepository;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HistoryService {
    OrderDbRepository orderDbRepository;
    UserRepository userRepository;

    public HistoryService(@Autowired OrderDbRepository orderDbRepository, @Autowired UserRepository userRepository) {
        this.orderDbRepository = orderDbRepository;
        this.userRepository = userRepository;
        log.debug("HistoryService created");
    }

    public List<OrderConfirmDto> getOrdersHistory(String userName) {
        log.debug("Getting orders history for user: {}", userName);
        Long userId = userRepository.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!")).getId();
        List<OrderEntity> all = orderDbRepository.findAllByUserIdOrderByOrderDate(userId);
        log.debug("Found {} orders for user: {}", all.size(), userName);

        return all.stream().map(DtoMapper::toDto).toList();
    }
}
