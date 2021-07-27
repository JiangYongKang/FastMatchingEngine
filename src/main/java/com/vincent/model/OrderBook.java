package com.vincent.model;

import lombok.Data;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

@Data
public class OrderBook {

    public static final ConcurrentSkipListSet<Order> ASK_ORDER_BUCKET = new ConcurrentSkipListSet<>(
            Comparator.comparing(Order::getUnitPrice).reversed()
                    .thenComparing(Order::getCreatedAt)
                    .thenComparing(Order::getId)
    );

    public static final ConcurrentSkipListSet<Order> BID_ORDER_BUCKET = new ConcurrentSkipListSet<>(
            Comparator.comparing(Order::getUnitPrice)
                    .thenComparing(Order::getCreatedAt).reversed()
                    .thenComparing(Order::getId)
    );

}
