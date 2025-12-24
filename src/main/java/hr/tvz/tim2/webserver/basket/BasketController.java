package hr.tvz.tim2.webserver.basket;

import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.ordering.services.OrderService;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = """
                    The entity will be reserved for 15 minutes.
                    Adding an existing entity will just refresh it's reservation expiration.
                    If the item is expired, reserving will be tried, but there is no guarantee the entity will still be available.""")
    @PutMapping(value = "/add/{movieId}")
    public ResponseEntity<?> addItem(@PathVariable String movieId,
                                     @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            basketService.addItemToBasket(userName, movieId);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/remove/{movieId}")
    public ResponseEntity<?> removeItem(@PathVariable String movieId,
                                        @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            basketService.removeItemFromBasket(userName, movieId);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/clear")
    public ResponseEntity<?> clearItems(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        try {
            basketService.removeAllItemsFromBasket(userName);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
