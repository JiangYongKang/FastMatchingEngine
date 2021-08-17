package com.vincent.web.service;

import com.vincent.engine.StreamMessageListenerContainerImpl;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import com.vincent.model.MemberOrder;
import com.vincent.model.OrderCommand;
import com.vincent.utils.SnowFlake;
import com.vincent.web.repository.MemberOrderRepository;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;

@Service
public class MemberOrderServiceImpl implements MemberOrderService {

    @Resource
    private MemberOrderRepository memberOrderRepository;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public OrderCommand createLimitOrder(OrderCommand command) {

        MemberOrder memberOrder = new MemberOrder();
        memberOrder.setSn(new SnowFlake().nextId().toString());
        memberOrder.setUid(command.getUid());
        memberOrder.setAction(command.getAction());
        memberOrder.setSymbol(command.getSymbol());
        memberOrder.setOrderPrice(command.getOrderPrice());
        memberOrder.setVolume(command.getAmount());
        memberOrder.setOriginVolume(command.getAmount());
        memberOrder.setState(OrderState.WAIT);
        memberOrder.setCategory(OrderCategory.LIMIT);
        memberOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        memberOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        memberOrderRepository.save(memberOrder);

        String key = String.format("CREATE_ORDERS:%s:%s:%s", command.getSymbol(), command.getCategory(), command.getAction());
        stringRedisTemplate.opsForZSet().add(key, memberOrder.getId().toString(), memberOrder.getCreatedAt().getTime());

        stringRedisTemplate.opsForStream().add(
                Record.of(command)
                        .withStreamKey(StreamMessageListenerContainerImpl.MATCHING_ENGINE_ORDERS_CHANNEL)
        );

        command.success();

        return command;
    }

    @Override
    public OrderCommand createMarketOrder(OrderCommand command) {
        return command;
    }

    @Override
    public OrderCommand cancelOrder(OrderCommand command) {
        return null;
    }
}
