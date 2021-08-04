package com.vincent.engine;

import com.vincent.config.StreamConsumerRunner;
import com.vincent.model.OrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OrderStreamMessageListener implements StreamListener<String, ObjectRecord<String, OrderCommand>> {

    @Resource
    private RedisTemplate<String, OrderCommand> redisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, OrderCommand> message) {
        RecordId recordId = message.getId();
        log.info("stream message => id: {}, stream: {}, body: {}", recordId, message.getStream(), message.getValue());
        redisTemplate.opsForStream().acknowledge(StreamConsumerRunner.MATCHING_ENGINE_ORDERS_GROUP, message);
    }

}
