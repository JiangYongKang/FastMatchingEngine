package com.vincent.engine;

import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.model.Order;
import com.vincent.model.OrderBook;
import com.vincent.model.OrderCommand;
import com.vincent.model.Trade;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderExchangeImpl implements OrderExchange {


    @Override
    public Trade doExchange(OrderCommand source, OrderCommand target) {



        BigDecimal minVolume = source.getAmount().min(target.getAmount());
        source.setAmount(source.getAmount().subtract(minVolume));
        target.setAmount(target.getAmount().subtract(minVolume));

        if (source.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            switch (source.getAction()) {
                case ASK:
                    OrderBook.ASK_ORDER_BUCKET.remove(source);
                case BID:
                    OrderBook.BID_ORDER_BUCKET.remove(target);
            }
        }
        if (target.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            switch (source.getAction()) {
                case ASK:
                    OrderBook.ASK_ORDER_BUCKET.remove(source);
                case BID:
                    OrderBook.BID_ORDER_BUCKET.remove(target);
            }
        }

        return new Trade(source.getId().toString(), target.getId().toString(), source.getOrderPrice(), minVolume);
    }

    @Override
    public void cancel(OrderCommand command) {

    }

    @Override
    public void cancel(String id) {

    }

    @Override
    public List<Trade> doMatching(OrderCommand command) {
        Set<Order> orders = OrderBook.ASK_ORDER_BUCKET.stream()
                .filter(s -> s.getUnitPrice().compareTo(command.getOrderPrice()) > 0)
                .collect(Collectors.toSet());
        return Collections.EMPTY_LIST;
    }
}
