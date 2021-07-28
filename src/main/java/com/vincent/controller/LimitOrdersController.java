package com.vincent.controller;

import com.vincent.engine.OrderExchange;
import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import com.vincent.model.CommonResponse;
import com.vincent.model.Order;
import com.vincent.model.Trade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/limit/orders")
public class LimitOrdersController {

    @Resource(name = "askOrderExchangeImpl")
    private OrderExchange askOrderExchange;

    @Resource(name = "bidOrderExchangeImpl")
    private OrderExchange bidOrderExchange;

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam("current") Integer current,
            @RequestParam("size") Integer size,
            @RequestParam("ticket") String ticket) {
        return ResponseEntity.ok(new CommonResponse());
    }

    @PostMapping("/ask")
    public ResponseEntity<?> createAsk(@RequestBody Order order) {
        order.setId(UUID.randomUUID().toString());
        order.setAction(OrderAction.ASK);
        order.setOriginVolume(order.getVolume());
        order.setCategory(OrderCategory.LIMIT);
        order.setState(OrderState.WAIT);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        List<Trade> trades = askOrderExchange.create(order);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setData(trades);
        return ResponseEntity.ok(commonResponse);
    }

    @PostMapping("/bid")
    public ResponseEntity<?> createBid(@RequestBody Order order) {
        order.setId(UUID.randomUUID().toString());
        order.setAction(OrderAction.BID);
        order.setOriginVolume(order.getVolume());
        order.setCategory(OrderCategory.LIMIT);
        order.setState(OrderState.WAIT);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        List<Trade> trades = askOrderExchange.create(order);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setData(trades);
        return ResponseEntity.ok(commonResponse);
    }

    @PutMapping
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok(new CommonResponse());
    }

}
