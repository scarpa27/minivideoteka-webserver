package hr.tvz.tim2.webserver.basket;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.BasketDto;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("basket")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class BasketController {
    private final BasketService basketService;

    public BasketController(@Autowired BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping()
    public ResponseEntity<BasketDto> getBasket(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        BasketDto dto = basketService.getBasketDto(userName);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = """
                    The entity will be reserved for 15 minutes.
                    Adding an existing entity will just refresh it's reservation expiration.
                    If the item is expired, reserving will be tried, but there is no guarantee the entity will still be available.""")
    @PutMapping(value = "/add/{movieId}")
    public ResponseEntity<Void> addItem(@PathVariable String movieId,
                                        @AuthenticationPrincipal ApplicationUser user) throws Exception {
        String userName = user.getUsername();
        basketService.addItemToBasket(userName, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/remove/{movieId}")
    public ResponseEntity<Void> removeItem(@PathVariable String movieId,
                                           @AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        basketService.removeItemFromBasket(userName, movieId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/clear")
    public ResponseEntity<Void> clearItems(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        basketService.removeAllItemsFromBasket(userName);
        return ResponseEntity.ok().build();
    }
}
