package com.vincent.db.service;

import com.vincent.db.repository.MemberOrderRepository;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import com.vincent.model.MemberOrder;
import com.vincent.model.OrderCommand;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class MemberOrderService {

    @Resource
    private MemberOrderRepository memberOrderRepository;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public OrderCommand createOrder(OrderCommand command) {

        MemberOrder memberOrder = new MemberOrder();
        memberOrder.setSn(UUID.randomUUID().toString());
        memberOrder.setUid(UUID.randomUUID().toString());
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
        redisTemplate.opsForZSet().add(key, memberOrder.getId(), memberOrder.getCreatedAt().getTime());

        command.setId(memberOrder.getId());
        command.setCode(200);
        command.setMessage("success");

        return command;

    }

}
