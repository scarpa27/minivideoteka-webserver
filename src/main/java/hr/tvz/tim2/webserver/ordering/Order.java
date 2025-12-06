package hr.tvz.tim2.webserver.ordering;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class Order {

    public Long userId;
    public List<String> itemIdList;
    public Instant orderDate;

    public Order(Long userId,
                 List<String> itemIdList,
                 Instant orderDate) {
        this.userId = userId;
        this.itemIdList = itemIdList;
        this.orderDate = orderDate;
    }

}
