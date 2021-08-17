package com.vincent.engine;

import com.vincent.model.OrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class StreamMessageListenerImpl implements StreamListener<String, ObjectRecord<String, OrderCommand>> {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, OrderCommand> message) {
        OrderCommand command = message.getValue();
        stringRedisTemplate.opsForStream().acknowledge(StreamMessageListenerContainerImpl.MATCHING_ENGINE_ORDERS_GROUP, message);
    }

}
