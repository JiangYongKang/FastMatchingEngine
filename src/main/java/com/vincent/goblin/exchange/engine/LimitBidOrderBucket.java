/*
 * Copyright 2019 Vincent Jiang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vincent.goblin.exchange.engine;

import com.vincent.goblin.exchange.model.Order;
import com.vincent.goblin.exchange.model.OrderState;
import com.vincent.goblin.exchange.model.Trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LimitBidOrderBucket extends LimitOrderBucket {

    @Override
    protected Trade doExchange(Order source, Order target) {

        Long exchangeAmount = Math.min(source.getAmount(), target.getAmount());
        source.setAmount(source.getAmount() - exchangeAmount);
        target.setAmount(target.getAmount() - exchangeAmount);

        OrderBook.BID_DEPTH.computeIfPresent(source.getPrice(), (k, v) -> v - exchangeAmount);
        OrderBook.ASK_DEPTH.computeIfPresent(target.getPrice(), (k, v) -> v - exchangeAmount);

        if (source.getAmount() == 0) {
            source.setState(OrderState.DONE);
            OrderBook.BID_ORDERS.remove(source);
        }

        if (target.getAmount() == 0) {
            target.setState(OrderState.DONE);
            OrderBook.ASK_ORDERS.remove(target);
        }

        return new Trade(source.getSn(), target.getSn(), target.getPrice(), exchangeAmount);
    }

    @Override
    public List<Trade> create(Order order) {

        if (OrderBook.BID_ORDERS.contains(order)) {
            return Collections.emptyList();
        }
        OrderBook.BID_DEPTH.merge(order.getPrice(), order.getAmount(), Long::sum);

        List<Trade> trades = new ArrayList<>();
        for (Order targetOrder : OrderBook.ASK_ORDERS) {
            if (order.getState().equals(OrderState.DONE) || order.getPrice() > targetOrder.getPrice()) {
                break;
            }
            trades.add(doExchange(order, targetOrder));
        }
        if (order.getState().equals(OrderState.WAIT)) {
            OrderBook.BID_ORDERS.add(order);
        }

        return trades;
    }

    @Override
    public void remove(String sn) {
        Order order = OrderBook.BID_ORDERS.stream().filter(e -> e.getSn().equals(sn))
                .findAny().orElseThrow(RuntimeException::new);
        OrderBook.BID_ORDERS.remove(order);
        OrderBook.BID_DEPTH.computeIfPresent(order.getPrice(), (k, v) -> v - order.getAmount());
    }
}
