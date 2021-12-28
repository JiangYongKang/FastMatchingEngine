/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fast.matching.engine;

import java.math.BigDecimal;
import java.util.*;

import static com.fast.matching.engine.OrderAction.ASK;

public class OrderBook {

    /**
     * 成交记录
     */
    private final List<Trade> trades;

    /**
     * 卖单集合
     */
    private final NavigableMap<BigDecimal, OrderBucket> askOrderBucket;

    /**
     * 买单集合
     */
    private final NavigableMap<BigDecimal, OrderBucket> bidOrderBucket;

    /**
     * ID => 订单映射
     */
    private final Map<Long, Order> idMaps;

    public OrderBook() {
        this.trades = new ArrayList<>();
        this.askOrderBucket = new TreeMap<>();
        this.bidOrderBucket = new TreeMap<>(Collections.reverseOrder());
        this.idMaps = new HashMap<>();
    }

    public List<Trade> newOrder(Long id, Long uid, BigDecimal commissionPrice, BigDecimal commissionVolume, OrderAction action, Long timestamp) {

        if (this.idMaps.containsKey(id)) {
            return Collections.emptyList();
        }

        Order order = new Order(
                id,
                uid,
                commissionPrice,
                commissionVolume,
                action,
                timestamp
        );

        SortedMap<BigDecimal, OrderBucket> matchedBucket = this.matchingBucket(commissionPrice, action);
        Iterator<OrderBucket> iterator = matchedBucket.values().iterator();

        while (iterator.hasNext()) {
            OrderBucket orderBucket = iterator.next();
            List<Trade> trades = orderBucket.doExchange(order);
            this.trades.addAll(trades);
            if (orderBucket.volume().compareTo(BigDecimal.ZERO) == 0) {
                iterator.remove();
            }
            if (order.commissionVolume().compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        if (order.commissionVolume().compareTo(BigDecimal.ZERO) > 0) {
            SortedMap<BigDecimal, OrderBucket> bucketByAction = this.findBucketByAction(action);
            OrderBucket newBucket = bucketByAction.getOrDefault(commissionPrice, new OrderBucket());
            newBucket.setOrder(order);
            bucketByAction.put(commissionPrice, newBucket);
            idMaps.put(id, order);
        }

        return trades;
    }

    public void cancelOrder(Long id) {
        Order order = idMaps.get(id);
        SortedMap<BigDecimal, OrderBucket> bucketMap = findBucketByAction(order.action());
        OrderBucket orderBucket = bucketMap.get(order.commissionPrice());
        orderBucket.cancel(id);
        if (orderBucket.volume().compareTo(BigDecimal.ZERO) == 0) {
            bucketMap.remove(order.commissionPrice());
        }
    }

    private SortedMap<BigDecimal, OrderBucket> matchingBucket(BigDecimal commissionPrice, OrderAction action) {
        return action.equals(ASK) ? bidOrderBucket.headMap(commissionPrice, true) : askOrderBucket.headMap(commissionPrice, true);
    }

    private SortedMap<BigDecimal, OrderBucket> findBucketByAction(OrderAction action) {
        return action.equals(ASK) ? askOrderBucket : bidOrderBucket;
    }

    public List<Trade> trades() {
        return trades;
    }

    public SortedMap<BigDecimal, OrderBucket> askOrderBucket() {
        return askOrderBucket;
    }

    public SortedMap<BigDecimal, OrderBucket> bidOrderBucket() {
        return bidOrderBucket;
    }
}
