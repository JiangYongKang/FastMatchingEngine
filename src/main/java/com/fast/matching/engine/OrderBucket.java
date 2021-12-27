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

public class OrderBucket {

    private BigDecimal volume;
    private final Map<Long, Order> entries;

    public OrderBucket() {
        this.volume = BigDecimal.ZERO;
        this.entries = new LinkedHashMap<>();
    }

    public List<Trade> doExchange(Order source) {

        List<Trade> trades = new ArrayList<>();
        Iterator<Order> iterator = entries.values().iterator();

        while (iterator.hasNext()) {
            Order target = iterator.next();
            if (!source.uid().equals(target.uid())) {
                BigDecimal minVolume = this.matchTo(source, target);
                this.volume = this.volume.subtract(minVolume);
                Trade trade = new Trade(source.id(), target.id(), target.commissionPrice(), minVolume, source.action());
                trades.add(trade);
                if (target.commissionVolume().compareTo(BigDecimal.ZERO) == 0) {
                    iterator.remove();
                }
                if (source.commissionVolume().compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
            }
        }

        return trades;
    }

    public void cancel(Long id) {
        Order order = entries.get(id);
        this.volume = this.volume.subtract(order.commissionVolume());
        entries.remove(id);
    }

    private BigDecimal matchTo(Order source, Order target) {
        BigDecimal minVolume = source.commissionVolume().min(target.commissionVolume());
        source.setCommissionVolume(source.commissionVolume().subtract(minVolume));
        target.setCommissionVolume(target.commissionVolume().subtract(minVolume));
        return minVolume;
    }

    public void setOrder(Order order) {
        this.entries.put(order.id(), order);
        this.volume = this.volume.add(order.commissionVolume());
    }

    public BigDecimal volume() {
        return volume;
    }
}
