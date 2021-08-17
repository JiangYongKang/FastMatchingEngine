package com.vincent.model;

import com.vincent.enums.Coin;
import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
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

    public static Order buildOf(OrderCommand command) {
        return Order.builder()
                .id(command.getId().toString())
                .action(command.getAction())
                .build();
    }

    public boolean isDone() {
        return this.state.equals(OrderState.DONE);
    }

    public boolean isWait() {
        return this.state.equals(OrderState.WAIT);
    }

    public void subtractVolume(BigDecimal volume) {
        this.volume = this.volume.subtract(volume);
        if (this.volume.compareTo(BigDecimal.ZERO) == 0) {
            this.state = OrderState.DONE;
        }
    }

}
