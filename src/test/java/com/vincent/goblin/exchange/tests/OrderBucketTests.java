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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
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

        bidOrderBucket.create(new Order(4, 9019L, 4L, OrderAction.BID, OrderCategory.LIMIT));
        bidOrderBucket.create(new Order(5, 9018L, 3L, OrderAction.BID, OrderCategory.LIMIT));
        bidOrderBucket.create(new Order(6, 9017L, 1L, OrderAction.BID, OrderCategory.LIMIT));

        askOrderBucket.create(new Order(1, 9013L, 2L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(2, 9012L, 3L, OrderAction.ASK, OrderCategory.LIMIT));
        askOrderBucket.create(new Order(3, 9011L, 2L, OrderAction.ASK, OrderCategory.LIMIT));
    }

    @Test
    public void shouldMatchOrders() {
        Order bidOrder = new Order(10, 9012L, 6L, OrderAction.BID, OrderCategory.LIMIT);
        List<Trade> trades = bidOrderBucket.create(bidOrder);
        Assert.assertEquals(OrderBook.BID_DEPTH.get(bidOrder.getPrice()).longValue(), 1L);
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 1);
        Assert.assertEquals(OrderBook.ASK_ORDERS.first().getPrice().longValue(), 9011L);
        Assert.assertEquals(OrderBook.ASK_ORDERS.first().getAmount().longValue(), 2L);
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 4);
        Assert.assertEquals(OrderBook.BID_ORDERS.last().getPrice().longValue(), 9012L);
        Assert.assertEquals(OrderBook.BID_ORDERS.last().getAmount().longValue(), 1L);
        Assert.assertEquals(OrderBook.ASK_DEPTH.get(9013L).longValue(), 0L);
        Assert.assertEquals(OrderBook.ASK_DEPTH.get(9012L).longValue(), 0L);
        Assert.assertEquals(trades.size(), 2);
    }

    @Test
    public void shouldIgnoreDuplicateOrderSn() {
        Order bidOrder = new Order(6, 9017L, 1L, OrderAction.BID, OrderCategory.LIMIT);
        bidOrderBucket.create(bidOrder);
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 4);
        bidOrderBucket.create(bidOrder);
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 4);
        Order askOrder = new Order(1, 9013L, 2L, OrderAction.ASK, OrderCategory.LIMIT);
        askOrderBucket.create(askOrder);
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 4);
        askOrderBucket.create(askOrder);
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 4);
    }

    @Test
    public void shouldRemoveOrder() {
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 3);
        Assert.assertEquals(OrderBook.BID_DEPTH.get(9017L).longValue(), 1L);
        Order bidOrder = new Order(10, 9017L, 1L, OrderAction.BID, OrderCategory.LIMIT);
        bidOrderBucket.create(bidOrder);
        Assert.assertEquals(OrderBook.BID_DEPTH.get(9017L).longValue(), 2L);
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 4);
        bidOrderBucket.remove(bidOrder.getSn());
        Assert.assertEquals(OrderBook.BID_DEPTH.get(9017L).longValue(), 1L);
        Assert.assertEquals(OrderBook.BID_ORDERS.size(), 3);
        Assert.assertEquals(OrderBook.ASK_DEPTH.get(9013L).longValue(), 2L);
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 3);
        Order askOrder = new Order(10, 9013L, 1L, OrderAction.ASK, OrderCategory.LIMIT);
        askOrderBucket.create(askOrder);
        Assert.assertEquals(OrderBook.ASK_DEPTH.get(9013L).longValue(), 3L);
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 4);
        askOrderBucket.remove(askOrder.getSn());
        Assert.assertEquals(OrderBook.ASK_DEPTH.get(9013L).longValue(), 2L);
        Assert.assertEquals(OrderBook.ASK_ORDERS.size(), 3);
    }

    @Test
    public void shouldAccumulateDepth() {
        Assert.assertEquals(OrderBook.BID_DEPTH.size(), 3);
        Assert.assertEquals(OrderBook.ASK_DEPTH.size(), 3);
        Assert.assertEquals(4L, OrderBook.BID_DEPTH.get(9019L).longValue());
        Assert.assertEquals(3L, OrderBook.BID_DEPTH.get(9018L).longValue());
        Assert.assertEquals(1L, OrderBook.BID_DEPTH.get(9017L).longValue());
        Assert.assertEquals(2L, OrderBook.ASK_DEPTH.get(9013L).longValue());
        Assert.assertEquals(3L, OrderBook.ASK_DEPTH.get(9012L).longValue());
        Assert.assertEquals(2L, OrderBook.ASK_DEPTH.get(9011L).longValue());
        bidOrderBucket.create(new Order(6, 9017L, 1L, OrderAction.BID, OrderCategory.LIMIT));
        Assert.assertEquals(2L, OrderBook.BID_DEPTH.get(9017L).longValue());
    }

    @Test
    public void OrderMatchPressureTest() {
        for (int i = 0; i < 100000; i++) {
            askOrderBucket.create(new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 10000), RandomUtils.nextLong(1, 10), OrderAction.ASK, OrderCategory.LIMIT));
            bidOrderBucket.create(new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 10000), RandomUtils.nextLong(1, 10), OrderAction.BID, OrderCategory.LIMIT));
        }
        log.info("ASK ORDERS SIZE: {}", OrderBook.ASK_ORDERS.size());
        log.info("BID ORDERS SIZE: {}", OrderBook.BID_ORDERS.size());
    }

    @Test
    public void OrderMatchPressureTest2() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100000; i++) {
            executorService.execute(() -> {
                askOrderBucket.create(new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 10000), RandomUtils.nextLong(1, 10), OrderAction.ASK, OrderCategory.LIMIT));
                bidOrderBucket.create(new Order(RandomUtils.nextInt(1, 1000000), RandomUtils.nextLong(8000, 10000), RandomUtils.nextLong(1, 10), OrderAction.BID, OrderCategory.LIMIT));
            });
        }
        executorService.awaitTermination(2, TimeUnit.SECONDS);
        log.info("ASK ORDERS SIZE: {}", OrderBook.ASK_ORDERS.size());
        log.info("BID ORDERS SIZE: {}", OrderBook.BID_ORDERS.size());
    }

}
