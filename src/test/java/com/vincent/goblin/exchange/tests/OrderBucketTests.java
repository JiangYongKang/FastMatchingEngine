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

package com.vincent.goblin.exchange.tests;

import com.vincent.goblin.exchange.engine.LimitAskOrderBucket;
import com.vincent.goblin.exchange.engine.LimitBidOrderBucket;
import com.vincent.goblin.exchange.engine.OrderBook;
import com.vincent.goblin.exchange.engine.OrderBucket;
import com.vincent.goblin.exchange.model.Order;
import com.vincent.goblin.exchange.model.OrderAction;
import com.vincent.goblin.exchange.model.OrderCategory;
import com.vincent.goblin.exchange.model.Trade;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class OrderBucketTests {

    private OrderBucket askOrderBucket;
    private OrderBucket bidOrderBucket;

    @Before
    public void initializeOrderBook() {

        OrderBook.BID_ORDERS.clear();
        OrderBook.ASK_ORDERS.clear();
        OrderBook.BID_DEPTH.clear();
        OrderBook.ASK_DEPTH.clear();

        askOrderBucket = new LimitAskOrderBucket();
        bidOrderBucket = new LimitBidOrderBucket();

        bidOrderBucket.create(new Order(6, 9019L, 4L, OrderAction.BID, OrderCategory.LIMIT));
        bidOrderBucket.create(new Order(7, 9018L, 3L, OrderAction.BID, OrderCategory.LIMIT));
        bidOrderBucket.create(new Order(8, 9017L, 1L, OrderAction.BID, OrderCategory.LIMIT));
        bidOrderBucket.create(new Order(9, 9016L, 2L, OrderAction.BID, OrderCategory.LIMIT));
        bidOrderBucket.create(new Order(10, 9015L, 4L, OrderAction.BID, OrderCategory.LIMIT));

        askOrderBucket.create(new Order(1, 9013L, 2L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(2, 9012L, 3L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(3, 9011L, 2L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(4, 9010L, 1L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(5, 9010L, 4L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(5, 9009L, 4L, OrderAction.ASK, OrderCategory.LIMIT));
    }

    @Test
    public void shouldMatchOrders() {
        for (int i = 0; i < 10000; i++) {
            Order askOrder = new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 12000), RandomUtils.nextLong(1, 10), OrderAction.ASK, OrderCategory.LIMIT);
            List<Trade> tradesOne = askOrderBucket.create(askOrder);
        }
        for (int i = 0; i < 10000; i++) {
            Order bidOrder = new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 12000), RandomUtils.nextLong(1, 10), OrderAction.BID, OrderCategory.LIMIT);
            List<Trade> tradesTwo = bidOrderBucket.create(bidOrder);
        }
        for (int i = 0; i < 100000; i++) {
            Order askOrder = new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 12000), RandomUtils.nextLong(1, 10), OrderAction.ASK, OrderCategory.LIMIT);
            Order bidOrder = new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 12000), RandomUtils.nextLong(1, 10), OrderAction.BID, OrderCategory.LIMIT);
            List<Trade> tradesOne = askOrderBucket.create(askOrder);
            List<Trade> tradesTwo = bidOrderBucket.create(bidOrder);
        }
    }

    @Test
    public void shouldAccumulateDepth() {
        Assert.assertEquals(OrderBook.BID_DEPTH.size(), 5);
        Assert.assertEquals(OrderBook.ASK_DEPTH.size(), 5);
        Assert.assertEquals(5L, OrderBook.ASK_DEPTH.get(9010L).longValue());
        Assert.assertEquals(2L, OrderBook.ASK_DEPTH.get(9013L).longValue());
        Assert.assertEquals(4L, OrderBook.BID_DEPTH.get(9015L).longValue());
        Assert.assertEquals(4L, OrderBook.BID_DEPTH.get(9019L).longValue());
    }

    @Test
    public void shouldRemoveOrder() {
        Order bidOrder = new Order(10, 9015L, 1L, OrderAction.BID, OrderCategory.LIMIT);
        bidOrderBucket.create(bidOrder);
        bidOrderBucket.remove(bidOrder.getSn());
        Order askOrder = new Order(10, 9013L, 1L, OrderAction.ASK, OrderCategory.LIMIT);
        askOrderBucket.create(askOrder);
        askOrderBucket.remove(askOrder.getSn());
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 5);
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 5);
    }

}
