package com.vincent.engine;

import com.vincent.model.OrderCommand;
import com.vincent.model.Trade;

import java.util.List;

public interface OrderExchange {

    Trade doExchange(OrderCommand source, OrderCommand target);

    void cancel(OrderCommand command);

    void cancel(String id);

    List<Trade> doMatching(OrderCommand command);

}
