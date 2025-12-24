package hr.tvz.tim2.webserver.ordering.services;

import hr.tvz.tim2.webserver.dto.DtoMapper;
import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.ordering.entities.OrderEntity;
import hr.tvz.tim2.webserver.ordering.repositories.OrderDbRepository;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {
    OrderDbRepository orderDbRepository;
    UserRepository userRepository;

    public HistoryService(@Autowired OrderDbRepository orderDbRepository, @Autowired UserRepository userRepository) {
        this.orderDbRepository = orderDbRepository;
        this.userRepository = userRepository;
    }

    public List<OrderConfirmDto> getOrdersHistory(String userName) {
        Long userId = userRepository.findByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User doesn't exist!")).getId();
        List<OrderEntity> all = orderDbRepository.findAllByUserIdOrderByOrderDate(userId);

        return all.stream().map(DtoMapper::toDto).toList();
    }
}
