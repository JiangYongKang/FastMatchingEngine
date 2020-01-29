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

package com.vincent.goblin.exchange.model;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
public class Order {

    /**
     * 订单
     */
    private String sn;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 挂单价
     */
    private Long price;

    /**
     * 委托量
     */
    private Long amount;

    /**
     * 原委托量
     */
    private Long originalAmount;

    /**
     * 交易类型
     */
    private OrderAction action;

    /**
     * 订单类型
     */
    private OrderCategory category;

    /**
     * 订单状态
     */
    private OrderState state;

    /**
     * 创建时间
     */
    private Long createdAt;

    public Order() {
    }

    public Order(Integer uid, Long price, Long amount, OrderAction action, OrderCategory category) {
        this.sn = RandomStringUtils.randomAlphabetic(32);
        this.uid = uid;
        this.price = price;
        this.amount = amount;
        this.originalAmount = amount;
        this.action = action;
        this.category = category;
        this.state = OrderState.WAIT;
        this.createdAt = System.currentTimeMillis();
    }
}
