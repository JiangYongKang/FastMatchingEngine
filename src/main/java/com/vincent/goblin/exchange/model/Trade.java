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
public class Trade {

    /**
     * 交易ID
     */
    private String sn;

    /**
     * 买入方ID
     */
    private String askSn;

    /**
     * 卖出方ID
     */
    private String bidSn;

    /**
     * 成交价
     */
    private Long price;

    /**
     * 成交量
     */
    private Long amount;

    /**
     * 创建时间
     */
    private Long createdAt;

    public Trade(String askSn, String bidSn, Long price, Long amount) {
        this.sn = RandomStringUtils.randomAlphabetic(18);
        this.askSn = askSn;
        this.bidSn = bidSn;
        this.price = price;
        this.amount = amount;
        this.createdAt = System.currentTimeMillis();
    }
}
