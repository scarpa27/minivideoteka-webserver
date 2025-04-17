package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.ordering.OrderService;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("basket")
public class BasketController {

    private final BasketService basketService;
    private final OrderService orderService;

    public BasketController(@Autowired BasketService basketService,
                            @Autowired OrderService orderService) {
        this.basketService = basketService;
        this.orderService = orderService;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> addItem(@RequestBody String movieId,
                                     @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        System.out.printf("Username for basket: %s%n", userName);
        try {
            basketService.addItemToBasket(userName, movieId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/remove")
    public ResponseEntity<?> removeItem(@RequestBody String movieId) {
        try {
            basketService.removeItemFromBasket(69696969L, movieId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/clear")
    public ResponseEntity<?> clearItems(@RequestBody String userId) {
        try {
            basketService.removeAllItemsFromBasket(69696969L);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/completeOrder")
    public ResponseEntity<?> completeOrder(@RequestBody String userId) {
        String trackingNumber;
        try {
            trackingNumber = orderService.confirmOrder(userId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().body(trackingNumber);
    }

    @PostMapping(value = "/returnLatestOrder")
    public ResponseEntity<?> returnLatestOrder(@RequestBody String userId) {
        String trackingNumber;
        try {
            trackingNumber = orderService.returnOrder(userId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().body(trackingNumber);
    }


}
