package com.vincent.engine;

import com.vincent.model.Order;
import com.vincent.model.Trade;

import java.util.List;

public interface OrderExchange {

    Trade doExchange(Order source, Order target);

    List<Trade> create(Order order);

    void cancel(Order order);

    void cancel(String id);

}
