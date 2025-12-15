package hr.tvz.tim2.webserver.chrono;

import hr.tvz.tim2.webserver.basket.logic.BasketDbRepository;
import hr.tvz.tim2.webserver.basket.logic.BasketItemDbRepository;
import hr.tvz.tim2.webserver.security.repository.UserRepository;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
        System.out.printf("Cleaning expired reservations at %s%n", now);
        itemRepo.findAllByReservedUntilBefore(now).forEach(item -> {
            System.out.printf("Removing expired reservation for item %s%n", item.getItemId());
            stockService.freeUpMovie(item.getItemId());
            itemRepo.delete(item);
            itemRepo.flush();
        });
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 10 * 1000)
    public void cleanupExpiredBaskets() {
        var now = Instant.now();
        System.out.printf("CLeaning expired baskets at %s%n", now);
        basketRepo.deleteAllByValidUntilDateBefore(now);
    }
}
