package hr.tvz.tim2.webserver.basket.logic;

import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.ordering.OrderService;
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
                    Ovaj šteka kad se naglo dodaje film kojeg baš nema. 
                    Inače je ideja da rezervacija traje 15 min, a ako ga opet dodaš, 
                    da se samo osvježi vrime rezervacije, al zbog nekog razloga ako klikneš dva tri puta brzo 
                    film koji je svakako bio samo jedan na stanju, sjebe se nešto, više se nikad ni ne oslobodi film iz rezervacije. """)
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

    @PostMapping(value = "/completeOrder")
    public ResponseEntity<?> completeOrder(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        OrderConfirmDto dto;
        try {
            dto = orderService.confirmOrder(userName);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = """
                    Ovo ne radi uopće. Nisan ga još implementira.""")
    @PostMapping(value = "/returnLatestOrder")
    public ResponseEntity<?> returnLatestOrder(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        String trackingNumber;
        try {
            trackingNumber = orderService.returnOrder(userName);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().body(trackingNumber);
    }
}
