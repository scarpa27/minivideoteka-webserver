package hr.tvz.tim2.webserver.ordering;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.MovieDto;
import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.dto.RichOrderConfirmDto;
import hr.tvz.tim2.webserver.movie.MovieService;
import hr.tvz.tim2.webserver.ordering.services.HistoryService;
import hr.tvz.tim2.webserver.ordering.services.OrderService;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("order")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class OrderController {
    private final OrderService orderService;
    private final HistoryService historyService;
    private final MovieService movieService;

    public OrderController(@Autowired OrderService orderService,
                           @Autowired HistoryService historyService,
                           @Autowired MovieService movieService) {
        this.orderService = orderService;
        this.historyService = historyService;
        this.movieService = movieService;
        log.debug("OrderController created");
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderConfirmDto>> getOrdersHistory(@AuthenticationPrincipal ApplicationUser user) {
        log.debug("Getting orders history for user: {}", user.getUsername());
        String username = user.getUsername();
        List<OrderConfirmDto> history = historyService.getOrdersHistory(username);
        return ResponseEntity.ok().body(history);
    }

    @GetMapping("/history/v2")
    public ResponseEntity<List<RichOrderConfirmDto>> getOrdersHistoryWithMovieName(@AuthenticationPrincipal ApplicationUser user) {
        log.debug("Getting orders history with movie names included for user: {}", user.getUsername());
        String username = user.getUsername();
        List<OrderConfirmDto> history = historyService.getOrdersHistory(username);
        List<RichOrderConfirmDto> finalList = new ArrayList<>();
        for (var dto : history) {
            List<String> movieIds = dto.getItemIdList().stream().toList();
            List<MovieDto> movies = movieService.getSpecificListDto(movieIds);

            var rich = new RichOrderConfirmDto(dto, movies);
            finalList.add(rich);
        }

        return ResponseEntity.ok().body(finalList);
    }

    @Operation(summary = "Ova putanja je prominila lokaciju!!!")
    @PostMapping(value = "/completeOrder")
    public ResponseEntity<OrderConfirmDto> completeOrder(@AuthenticationPrincipal ApplicationUser user) {
        log.debug("Confirming order for user: {}", user.getUsername());
        String userName = user.getUsername();
        var dto = orderService.confirmOrder(userName);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "Ova putanja je prominila lokaciju!!!")
    @PostMapping(value = "/returnLatestOrder")
    public ResponseEntity<OrderConfirmDto> returnLatestOrder(@AuthenticationPrincipal ApplicationUser user) {
        log.debug("Returning latest order for user: {}", user.getUsername());
        String userName = user.getUsername();
        var orderConfirm = orderService.returnOrder(userName);
        return ResponseEntity.ok().body(orderConfirm);
    }
}
