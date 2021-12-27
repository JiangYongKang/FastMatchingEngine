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

public class Trade {

    private Long sourceOrderId;
    private Long targetOrderId;
    private BigDecimal commissionPrice;
    private BigDecimal commissionVolume;
    private OrderAction action;
    private Long timestamp;

    public Trade() {
    }

    public Trade(Long sourceOrderId, Long targetOrderId, BigDecimal commissionPrice, BigDecimal commissionVolume, OrderAction action) {
        this.sourceOrderId = sourceOrderId;
        this.targetOrderId = targetOrderId;
        this.commissionPrice = commissionPrice;
        this.commissionVolume = commissionVolume;
        this.action = action;
        this.timestamp = System.currentTimeMillis();
    }

    public Long sourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(Long sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public Long targetOrderId() {
        return targetOrderId;
    }

    public void setTargetOrderId(Long targetOrderId) {
        this.targetOrderId = targetOrderId;
    }

    public BigDecimal commissionPrice() {
        return commissionPrice;
    }

    public void setCommissionPrice(BigDecimal commissionPrice) {
        this.commissionPrice = commissionPrice;
    }

    public BigDecimal commissionVolume() {
        return commissionVolume;
    }

    public void setCommissionVolume(BigDecimal commissionVolume) {
        this.commissionVolume = commissionVolume;
    }

    public OrderAction action() {
        return action;
    }

    public void setAction(OrderAction action) {
        this.action = action;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
