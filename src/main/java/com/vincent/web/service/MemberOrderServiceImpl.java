package com.vincent.web.service;

import com.vincent.config.StreamConsumerRunner;
import com.vincent.enums.OrderCategory;
import com.vincent.enums.OrderState;
import com.vincent.model.MemberOrder;
import com.vincent.model.OrderCommand;
import com.vincent.utils.SnowFlake;
import com.vincent.web.repository.MemberOrderRepository;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Collections;

@Service
public class MemberOrderServiceImpl implements MemberOrderService {

    @Resource
    private MemberOrderRepository memberOrderRepository;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        redisTemplate.opsForZSet().add(key, memberOrder.getId(), memberOrder.getCreatedAt().getTime());

        command.setId(memberOrder.getId());
        command.setCode(200);
        command.setMessage("success");


        MapRecord<String, String, OrderCommand> record = StreamRecords.newRecord().in(StreamConsumerRunner.MATCHING_ENGINE_ORDERS_CHANNEL)
                .ofMap(Collections.singletonMap(memberOrder.getSn(), command));
        RecordId recordId = redisTemplate.opsForStream().add(record);

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
