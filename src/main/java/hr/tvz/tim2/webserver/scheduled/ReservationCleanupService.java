package hr.tvz.tim2.webserver.scheduled;

import hr.tvz.tim2.webserver.basket.BasketDbRepository;
import hr.tvz.tim2.webserver.basket.BasketItemDbRepository;
import hr.tvz.tim2.webserver.basket.BasketStatus;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class ReservationCleanupService {
    private final StockService stockService;
    private final BasketDbRepository basketRepo;
    private final BasketItemDbRepository itemRepo;
    private final UserRepository userRepo;

    public ReservationCleanupService(@Autowired StockService stockService,
                                     @Autowired BasketDbRepository basketRepo,
                                     @Autowired BasketItemDbRepository itemRepo,
                                     @Autowired UserRepository userRepo) {
        this.stockService = stockService;
        this.basketRepo = basketRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void cleanupExpiredReservations() {
        var now = Instant.now();
        log.info("Cleaning expired reservations at {}", now);
        itemRepo.findAllByReservedUntilBeforeAndBasketStatusNot(now, BasketStatus.ORDERED).forEach(item -> {
            log.info("Removing expired reservation for item {}", item.getItemId());
            stockService.freeUpMovie(item.getItemId());
            itemRepo.delete(item);
            itemRepo.flush();
        });
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 10 * 1000)
    public void cleanupExpiredBaskets() {
        var now = Instant.now();
        log.info("Cleaning expired baskets at {}", now);
        basketRepo.deleteAllByValidUntilDateBeforeAndStatusNot(now, BasketStatus.ORDERED);
    }
}
