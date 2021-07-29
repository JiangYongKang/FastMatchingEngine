package com.vincent.web.controller;

import com.vincent.db.repository.MemberOrderRepository;
import com.vincent.engine.OrderExchange;
import com.vincent.enums.OrderAction;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import com.vincent.model.CommonResponse;
import com.vincent.model.MemberOrder;
import com.vincent.model.Order;
import com.vincent.model.Trade;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/limit/orders")
public class LimitOrdersController {

    @Resource(name = "askOrderExchangeImpl")
    private OrderExchange askOrderExchange;

    @Resource(name = "bidOrderExchangeImpl")
    private OrderExchange bidOrderExchange;

    @Resource
    private MemberOrderRepository memberOrderRepository;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam("current") Integer current,
            @RequestParam("size") Integer size,
            @RequestParam("ticket") String ticket) {
        return ResponseEntity.ok(new CommonResponse());
    }

    @PostMapping("/ask")
    public ResponseEntity<?> createAsk(@RequestBody Order order) {

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

        memberOrderRepository.save(memberOrder);

        redisTemplate.opsForZSet().add("ORDER_BOOK:BTC_USDT:ASK:ORDER_ID", memberOrder, memberOrder.getUnitPrice().doubleValue());

        CommonResponse commonResponse = new CommonResponse();
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
