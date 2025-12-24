package hr.tvz.tim2.webserver.ordering;

import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.ordering.services.HistoryService;
import hr.tvz.tim2.webserver.ordering.services.OrderService;
import hr.tvz.tim2.webserver.security.user.ApplicationUser;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;
    private final HistoryService historyService;

    public OrderController(@Autowired OrderService orderService,
                           @Autowired HistoryService historyService) {
        this.orderService = orderService;
        this.historyService = historyService;
    }

    @GetMapping("/history")
    public ResponseEntity<?> getOrdersHistory(@AuthenticationPrincipal ApplicationUser user) {
        String username = user.getUsername();
        try {
            var history = historyService.getOrdersHistory(username);
            return ResponseEntity.ok().body(history);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ova putanja je prominila lokaciju!!!")
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

    @Operation(summary = "Ova putanja je prominila lokaciju!!!")
    @PostMapping(value = "/returnLatestOrder")
    public ResponseEntity<?> returnLatestOrder(@AuthenticationPrincipal ApplicationUser user) {
        String userName = user.getUsername();
        OrderConfirmDto orderConfirm;
        try {
            orderConfirm = orderService.returnOrder(userName);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok().body(orderConfirm);
    }
}
