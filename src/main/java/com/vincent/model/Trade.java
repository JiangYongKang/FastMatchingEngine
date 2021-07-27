package com.vincent.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Trade {

    /**
     * transaction id
     */
    private String id;

    /**
     * ask order id
     */
    private String askOrderId;

    /**
     * bid order id
     */
    private String bidOrderId;

    /**
     * the final price
     */
    private BigDecimal finalPrice;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 创建时间
     */
    private Timestamp createdAt;

    public Trade() {
    }

    public Trade(String askOrderId, String bidOrderId, BigDecimal finalPrice, BigDecimal volume) {
        this.askOrderId = askOrderId;
        this.bidOrderId = bidOrderId;
        this.finalPrice = finalPrice;
        this.volume = volume;
    }
}
