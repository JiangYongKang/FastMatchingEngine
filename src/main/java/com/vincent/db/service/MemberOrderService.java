package com.vincent.db.service;

import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import com.vincent.model.MemberOrder;
import com.vincent.model.Order;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberOrderService {

    public void createOrder(Order order) {

        MemberOrder memberOrder = new MemberOrder();
        memberOrder.setSn(UUID.randomUUID().toString());
        memberOrder.setUid(UUID.randomUUID().toString());
        memberOrder.setAction(OrderAction.ASK);
        memberOrder.setCoin(order.getCoin());
        memberOrder.setUnitPrice(order.getUnitPrice());
        memberOrder.setVolume(order.getVolume());
        memberOrder.setOriginVolume(order.getVolume());
        memberOrder.setState(OrderState.WAIT);
        memberOrder.setCategory(OrderCategory.LIMIT);
    }

}
