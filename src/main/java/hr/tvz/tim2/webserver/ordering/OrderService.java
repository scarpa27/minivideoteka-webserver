package hr.tvz.tim2.webserver.ordering;

import hr.tvz.tim2.webserver.basket.logic.BasketEntity;
import hr.tvz.tim2.webserver.basket.logic.BasketService;
import hr.tvz.tim2.webserver.basket.logic.BasketStatus;
import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.membership.MemberService;
import hr.tvz.tim2.webserver.service.HistoryService;
import hr.tvz.tim2.webserver.service.MailingService;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

import static hr.tvz.tim2.webserver.dto.DtoMapper.toDto;

@Service
public class OrderService {
    private final BasketService basketService;
    private final MailingService mailingService;
    private final StockService stockService;
    private final HistoryService historyService;
    private final MemberService memberService;

    private final OrderDbRepository orderDbRepository;


    public OrderService(@Autowired BasketService basketService,
                        @Autowired MailingService mailingService,
                        @Autowired StockService stockService,
                        @Autowired HistoryService historyService,
                        @Autowired MemberService memberService,
                        @Autowired OrderDbRepository orderDbRepository) {
        this.basketService = basketService;
        this.mailingService = mailingService;
        this.stockService = stockService;
        this.historyService = historyService;
        this.memberService = memberService;
        this.orderDbRepository = orderDbRepository;
    }

    @Transactional
    public OrderConfirmDto confirmOrder(String userName) {
        Long userId = basketService.getUserId(userName);
        boolean canUserOrder = orderDbRepository.countAllByUserIdAndIsReturnedFalse(userId) <= 0;
        boolean isActiveMember = memberService.isUserActiveMember(userId);

        if (!canUserOrder)
            throw new IllegalStateException("User already has an active order");

        if(!isActiveMember)
            throw new IllegalStateException("User is not an active member");

        BasketEntity basket = basketService.getOrCreateActiveBasket(userName);
//        basket = basketService.refreshBasket(basket);
        int basketSize = basket.getBasketItems().size();

        if (basketSize < 1)
            throw new IllegalStateException("No items in the basket");
        if (basketSize > 3)
            throw new IllegalStateException("Too many items in the basket. Maximum is 3 per order.");

        var order = new OrderEntity();
        order.setOrderDate(Instant.now());
        order.setUser(basket.getUser());
        order.setIsReturned(false);
        order.setItemIdList(basket.getBasketItems().stream().map(bi -> {
            var oi = new OrderItemEntity();
            oi.setOrder(order);
            oi.setItemId(bi.getItemId());
            return oi;
        }).collect(Collectors.toSet()));
        basket.setStatus(BasketStatus.ORDERED);
        var trackingNumber = mailingService.generateTrackingNumber(basketSize);
        order.setOrderTracking(trackingNumber);

        orderDbRepository.saveAndFlush(order);

        var orderConfirmDto = new OrderConfirmDto();

        orderConfirmDto.setTrackingNumber(trackingNumber);
        orderConfirmDto.setOrderDate(order.getOrderDate());
        orderConfirmDto.setIsReturned(false);
        orderConfirmDto.setItemIdList(order.getItemIdList().stream()
                                              .map(OrderItemEntity::getItemId).collect(Collectors.toSet()));
        return orderConfirmDto;
    }

    public OrderConfirmDto returnOrder(String userName) {
        var userId = basketService.getUserId(userName);
        var orderOptional = orderDbRepository.findFirstByUserIdAndIsReturnedFalse(userId);

        if (orderOptional.isEmpty())
            throw new IllegalCallerException("The user has no active unreturned order.");
        var order = orderOptional.get();

        order.setIsReturned(true);
        order.setReturnDate(Instant.now());
        order.getItemIdList().forEach(oi -> stockService.freeUpMovie(oi.getItemId()));
        var trackingNumber = mailingService.generateTrackingNumber(0);
        order.setReturnTracking(trackingNumber);

        orderDbRepository.saveAndFlush(order);

        return toDto(order);
    }
}

