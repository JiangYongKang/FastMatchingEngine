package com.vincent.model;

import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.Symbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCommand {

    private Long id;
    private Long uid;
    private Symbol symbol;
    private BigDecimal orderPrice;
    private BigDecimal amount;
    private OrderAction action;
    private OrderCategory category;

    private int code;
    private String message;

    public static OrderCommand createOrder(Long uid, Symbol symbol, BigDecimal orderPrice, BigDecimal amount, OrderAction action, OrderCategory category) {
        return OrderCommand.builder()
                .uid(uid)
                .symbol(symbol)
                .orderPrice(orderPrice)
                .amount(amount)
                .action(action)
                .category(category)
                .build();
    }

    public static OrderCommand cancelOrder(Long id, Long uid) {
        return OrderCommand.builder()
                .id(id)
                .uid(uid)
                .build();
    }

}
