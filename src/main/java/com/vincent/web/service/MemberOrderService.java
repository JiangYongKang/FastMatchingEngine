package com.vincent.web.service;

import com.vincent.model.OrderCommand;

public interface MemberOrderService {

    OrderCommand createLimitOrder(OrderCommand command);

    OrderCommand createMarketOrder(OrderCommand command);

    OrderCommand cancelOrder(OrderCommand command);

}
