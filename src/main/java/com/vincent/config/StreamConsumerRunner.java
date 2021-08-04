package com.vincent.config;

import com.vincent.engine.OrderStreamMessageListener;
import com.vincent.model.OrderCommand;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;

@Component
public class StreamConsumerRunner implements ApplicationRunner, DisposableBean {

    public static final String MATCHING_ENGINE_ORDERS_CHANNEL = "MATCHING_ENGINE:ORDERS:CHANNEL";
    public static final String MATCHING_ENGINE_ORDERS_GROUP = "MATCHING_ENGINE:ORDERS:GROUP";

    @Resource
    private ThreadPoolTaskExecutor executor;
    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    @Resource
    private OrderStreamMessageListener orderStreamMessageListener;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, OrderCommand> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, OrderCommand>> containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10)
                        .executor(executor)
                        .pollTimeout(Duration.ZERO)
                        .targetType(OrderCommand.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, OrderCommand>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);

        initializeChannelAndGroup(redisTemplate.opsForStream());

        listenerContainer.receive(
                Consumer.from(MATCHING_ENGINE_ORDERS_GROUP, "CONSUMER-1"),
                StreamOffset.create(MATCHING_ENGINE_ORDERS_CHANNEL, ReadOffset.lastConsumed()),
                orderStreamMessageListener
        );

        listenerContainer.start();
    }

    private void initializeChannelAndGroup(StreamOperations<String, ?, ?> streamOperations) {
        String status = "OK";
        try {
            StreamInfo.XInfoGroups xInfoGroups = streamOperations.groups(StreamConsumerRunner.MATCHING_ENGINE_ORDERS_CHANNEL);
            if (xInfoGroups.stream().noneMatch(xInfoGroup -> StreamConsumerRunner.MATCHING_ENGINE_ORDERS_GROUP.equals(xInfoGroup.groupName()))) {
                status = streamOperations.createGroup(StreamConsumerRunner.MATCHING_ENGINE_ORDERS_CHANNEL, StreamConsumerRunner.MATCHING_ENGINE_ORDERS_GROUP);
            }
        } catch (Exception exception) {
            RecordId initialRecord = streamOperations.add(ObjectRecord.create(StreamConsumerRunner.MATCHING_ENGINE_ORDERS_CHANNEL, "Initial Record"));
            Assert.notNull(initialRecord, "Cannot initialize stream with key '" + StreamConsumerRunner.MATCHING_ENGINE_ORDERS_CHANNEL + "'");
            status = streamOperations.createGroup(StreamConsumerRunner.MATCHING_ENGINE_ORDERS_CHANNEL, ReadOffset.from(initialRecord), StreamConsumerRunner.MATCHING_ENGINE_ORDERS_GROUP);
        } finally {
            Assert.isTrue("OK".equals(status), "Cannot create group with name '" + StreamConsumerRunner.MATCHING_ENGINE_ORDERS_GROUP + "'");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}
