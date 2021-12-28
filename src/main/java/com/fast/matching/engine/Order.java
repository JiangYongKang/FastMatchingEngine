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

public class Order {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 委托价格
     */
    private BigDecimal commissionPrice;

    /**
     * 委托数量
     */
    private BigDecimal commissionVolume;

    /**
     * 订单方向
     */
    private OrderAction action;

    /**
     * 创建时间
     */
    private Long timestamp;

    public Order() {
    }

    public Order(Long id, Long uid, BigDecimal commissionPrice, BigDecimal commissionVolume, OrderAction action, Long timestamp) {
        this.id = id;
        this.uid = uid;
        this.commissionPrice = commissionPrice;
        this.commissionVolume = commissionVolume;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long uid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
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

    public Long timestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
