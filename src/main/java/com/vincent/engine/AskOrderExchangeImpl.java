package com.vincent.engine;

import com.vincent.model.Order;
import com.vincent.model.OrderBook;
import com.vincent.model.Trade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AskOrderExchangeImpl implements OrderExchange {

    @Override
    public Trade doExchange(Order source, Order target) {

        BigDecimal minVolume = source.getVolume().min(target.getVolume());
        source.setVolume(source.getVolume().subtract(minVolume));
        target.setVolume(target.getVolume().subtract(minVolume));

        if (source.getVolume().equals(BigDecimal.ZERO)) {
            OrderBook.ASK_ORDER_BUCKET.remove(source);
        }

        if (target.getVolume().equals(BigDecimal.ZERO)) {
            OrderBook.BID_ORDER_BUCKET.remove(target);
        }

        return new Trade(source.getId(), target.getId(), source.getUnitPrice(), minVolume);
    }

    @Override
    public List<Trade> create(Order order) {

        if (OrderBook.ASK_ORDER_BUCKET.contains(order)) {
            return Collections.emptyList();
        }

        List<Trade> trades = new ArrayList<>();
        for (Order targetOrder : OrderBook.BID_ORDER_BUCKET) {
            if (order.getVolume().equals(BigDecimal.ZERO) || order.getUnitPrice().compareTo(targetOrder.getUnitPrice()) < 0) {
                break;
            }
            trades.add(doExchange(order, targetOrder));
        }
        if (order.getVolume().compareTo(BigDecimal.ZERO) > 0) {
            OrderBook.ASK_ORDER_BUCKET.add(order);
        }

        return trades;
    }

    @Override
    public void cancel(Order order) {
        OrderBook.ASK_ORDER_BUCKET.remove(order);
    }

    @Override
    public void cancel(String id) {
        Order order = OrderBook.ASK_ORDER_BUCKET.stream().filter(e -> e.getId().equals(id))
                .findAny().orElseThrow(RuntimeException::new);
        this.cancel(order);
    }

}
