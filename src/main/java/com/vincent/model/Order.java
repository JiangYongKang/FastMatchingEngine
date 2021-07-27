package com.vincent.model;

import com.vincent.enums.Coin;
import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Order {

    private String id;
    private OrderAction action;
    private Coin coin;
    private BigDecimal unitPrice;
    private BigDecimal volume;
    private BigDecimal originVolume;
    private OrderState state;
    private OrderCategory category;

    private Timestamp updatedAt;
    private Timestamp createdAt;

}
