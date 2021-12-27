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

package com.fast.matching.engine.tests;

import com.fast.matching.engine.OrderBook;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fast.matching.engine.OrderAction.ASK;
import static com.fast.matching.engine.OrderAction.BID;
import static org.junit.jupiter.api.Assertions.*;

public class OrderBookTests {

    private final OrderBook orderBook;

    public OrderBookTests() {
        this.orderBook = new OrderBook();
    }

    @Test
    public void shouldCreateOrderBook() {
        assertNotNull(orderBook);
    }

    @Test
    public void shouldInitializeOrderBook() {
        assertEquals(orderBook.trades().size(), 0);
        assertEquals(orderBook.askOrderBucket().size(), 0);
        assertEquals(orderBook.bidOrderBucket().size(), 0);
    }

    @Test
    public void shouldNewOrder() {
        orderBook.newOrder(1L, 1L, BigDecimal.valueOf(100), BigDecimal.valueOf(0.1), ASK, System.currentTimeMillis());
        assertEquals(orderBook.trades().size(), 0);
        assertEquals(orderBook.askOrderBucket().size(), 1);
        assertEquals(orderBook.bidOrderBucket().size(), 0);
        assertTrue(orderBook.askOrderBucket().containsKey(BigDecimal.valueOf(100)));
    }

    @Test
    public void shouldCancelOrder() {
        orderBook.newOrder(1L, 1L, BigDecimal.valueOf(100), BigDecimal.valueOf(0.1), ASK, System.currentTimeMillis());
        orderBook.cancelOrder(1L);
        assertEquals(orderBook.trades().size(), 0);
        assertEquals(orderBook.askOrderBucket().size(), 0);
        assertEquals(orderBook.bidOrderBucket().size(), 0);
    }

    @Test
    public void shouldBeMatchingOrder() {
        orderBook.newOrder(1L, 1L, BigDecimal.valueOf(100), BigDecimal.valueOf(0.1), ASK, System.currentTimeMillis());
        orderBook.newOrder(2L, 1L, BigDecimal.valueOf(100), BigDecimal.valueOf(0.1), BID, System.currentTimeMillis());
        assertEquals(orderBook.trades().size(), 1);
        assertEquals(orderBook.askOrderBucket().size(), 0);
        assertEquals(orderBook.bidOrderBucket().size(), 0);
    }

}
