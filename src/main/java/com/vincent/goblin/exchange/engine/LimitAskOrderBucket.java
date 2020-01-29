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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LimitAskOrderBucket extends LimitOrderBucket {

    @Override
    protected Trade doExchange(Order source, Order target) {

        Long exchangeAmount = Math.max(source.getAmount(), target.getAmount());
        source.setAmount(source.getAmount() - exchangeAmount);
        target.setAmount(target.getAmount() - exchangeAmount);

        if (source.getAmount() == 0) {
            source.setState(OrderState.DONE);
            OrderBook.ASK_DEPTH.computeIfPresent(source.getPrice(), (k, v) -> v - exchangeAmount);
        }

        if (target.getAmount() == 0) {
            target.setState(OrderState.DONE);
            OrderBook.BID_DEPTH.computeIfPresent(target.getPrice(), (k, v) -> v - exchangeAmount);
        }

        return new Trade(source.getSn(), target.getSn(), target.getPrice(), exchangeAmount);
    }

    @Override
    public List<Trade> create(Order activeOrder) {
        OrderBook.ASK_DEPTH.merge(activeOrder.getPrice(), activeOrder.getAmount(), Long::sum);
        List<Trade> trades = OrderBook.BID_ORDERS.stream()
                .map(targetOrder -> {
                    if (activeOrder.getPrice() >= targetOrder.getPrice()) {
                        return doExchange(activeOrder, targetOrder);
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        OrderBook.BID_ORDERS.removeIf(order -> order.getState().equals(OrderState.DONE));
        if (activeOrder.getState().equals(OrderState.WAIT)) {
            OrderBook.ASK_ORDERS.add(activeOrder);
        }
        return trades;
    }

    @Override
    public void remove(String sn) {
        Order order = OrderBook.ASK_ORDERS.stream().filter(e -> e.getSn().equals(sn))
                .findAny().orElseThrow(RuntimeException::new);
        OrderBook.ASK_ORDERS.remove(order);
        OrderBook.ASK_DEPTH.computeIfPresent(order.getPrice(), (k, v) -> v - order.getAmount());
    }
}
