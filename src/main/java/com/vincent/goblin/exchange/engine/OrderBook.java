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

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public final class OrderBook {

    public static final ConcurrentSkipListSet<Order> ASK_ORDERS = new ConcurrentSkipListSet<>(Comparator.reverseOrder());
    public static final ConcurrentSkipListSet<Order> BID_ORDERS = new ConcurrentSkipListSet<>(Comparator.reverseOrder());

    public static final ConcurrentHashMap<Long, Long> ASK_DEPTH = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Long> BID_DEPTH = new ConcurrentHashMap<>();
}
