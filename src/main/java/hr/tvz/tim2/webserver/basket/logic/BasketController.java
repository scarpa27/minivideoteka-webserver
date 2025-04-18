package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.ordering.OrderService;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("basket")
public class BasketController {

    private final BasketService basketService;
    private final OrderService orderService;

    public BasketController(@Autowired BasketService basketService, @Autowired OrderService orderService) {
        this.basketService = basketService;
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<?> getBasket(@AuthenticationPrincipal ApplicationUser user) {
        try {
            String userName = user.getUsername();
            var dto = basketService.getBasketDto(userName);
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping(value = "/add/{movieId}")
    public ResponseEntity<Void> addItem(@PathVariable String movieId,
                                        @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            basketService.addItemToBasket(userName, movieId);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/remove/{movieId}")
    public ResponseEntity<Void> removeItem(@PathVariable String movieId,
                                           @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            basketService.removeItemFromBasket(userName, movieId);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/clear")
    public ResponseEntity<Void> clearItems(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            basketService.removeAllItemsFromBasket(userName);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/completeOrder")
    public ResponseEntity<?> completeOrder(@RequestBody String userId) {
        String trackingNumber;
        try {
            trackingNumber = orderService.confirmOrder(userId);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().body(trackingNumber);
    }

    @PostMapping(value = "/returnLatestOrder")
    public ResponseEntity<?> returnLatestOrder(@RequestBody String userId) {
        String trackingNumber;
        try {
            trackingNumber = orderService.returnOrder(userId);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().body(trackingNumber);
    }
}
