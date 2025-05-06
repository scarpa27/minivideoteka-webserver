package hr.tvz.tim2.webserver.ordering.services;

import hr.tvz.tim2.webserver.basket.BasketEntity;
import hr.tvz.tim2.webserver.basket.BasketItemEntity;
import hr.tvz.tim2.webserver.basket.BasketService;
import hr.tvz.tim2.webserver.dto.OrderConfirmDto;
import hr.tvz.tim2.webserver.membership.MemberService;
import hr.tvz.tim2.webserver.ordering.entities.OrderEntity;
import hr.tvz.tim2.webserver.ordering.entities.OrderItemEntity;
import hr.tvz.tim2.webserver.ordering.repositories.OrderDbRepository;
import hr.tvz.tim2.webserver.security.domain.User;
import hr.tvz.tim2.webserver.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock private BasketService basketService;
    @Mock private MailingService mailingService;
    @Mock private StockService stockService;
    @Mock private HistoryService historyService;
    @Mock private MemberService memberService;
    @Mock private OrderDbRepository orderDbRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void confirmOrder_shouldSucceed_whenAllValidationsPass() {
        String username = "user";
        long userId = 1L;

        BasketItemEntity item1 = new BasketItemEntity();
        item1.setItemId("nm100");
        BasketEntity basket = new BasketEntity();
        basket.setBasketItems(Set.of(item1));
        basket.setUser(new User()); // stub user

        when(basketService.getUserId(username)).thenReturn(userId);
        when(memberService.isUserBanned(userId)).thenReturn(false);
        when(memberService.isUserActiveMember(userId)).thenReturn(true);
        when(orderDbRepository.countAllByUserIdAndIsReturnedFalse(userId)).thenReturn(0);
        when(basketService.getOrCreateActiveBasket(username)).thenReturn(basket);
        when(mailingService.generateTrackingNumber(1)).thenReturn("ABC123");

        OrderConfirmDto result = orderService.confirmOrder(username);

        assertEquals("ABC123", result.getTrackingNumber());
        assertEquals(1, result.getItemIdList().size());
        assertFalse(result.getIsReturned());
        verify(orderDbRepository).saveAndFlush(any(OrderEntity.class));
    }

    @Test
    void confirmOrder_shouldFail_whenUserIsBanned() {
        when(basketService.getUserId("user")).thenReturn(1L);
        when(memberService.isUserBanned(1L)).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                orderService.confirmOrder("user"));
        assertEquals("User is banned", exception.getMessage());
    }

    @Test
    void confirmOrder_shouldFail_whenBasketIsEmpty() {
        long userId = 1L;
        when(basketService.getUserId("user")).thenReturn(userId);
        when(memberService.isUserBanned(userId)).thenReturn(false);
        when(orderDbRepository.countAllByUserIdAndIsReturnedFalse(userId)).thenReturn(0);
        when(memberService.isUserActiveMember(userId)).thenReturn(true);

        BasketEntity basket = new BasketEntity();
        basket.setBasketItems(Set.of());

        when(basketService.getOrCreateActiveBasket("user")).thenReturn(basket);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                                                   orderService.confirmOrder("user"));
        assertEquals("No items in the basket", exception.getMessage());
    }

    @Test
    void returnOrder_shouldSucceed_whenUserHasActiveOrder() {
        long userId = 1L;
        String username = "user";
        OrderItemEntity item = new OrderItemEntity();
        item.setItemId("nm100");

        OrderEntity order = new OrderEntity();
        order.setItemIdList(Set.of(item));
        order.setIsReturned(false);

        when(basketService.getUserId(username)).thenReturn(userId);
        when(orderDbRepository.findFirstByUserIdAndIsReturnedFalse(userId)).thenReturn(Optional.of(order));
        when(mailingService.generateTrackingNumber(0)).thenReturn("RET123");

        OrderConfirmDto result = orderService.returnOrder(username);

        assertTrue(result.getIsReturned());
        assertEquals("RET123", result.getReturnTrackingNumber());
        verify(stockService).freeUpMovie("nm100");
        verify(orderDbRepository).saveAndFlush(order);
    }
}