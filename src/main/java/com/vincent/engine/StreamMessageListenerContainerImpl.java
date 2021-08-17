package com.vincent.engine;

import com.vincent.model.OrderCommand;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;

@Component
public class StreamMessageListenerContainerImpl implements ApplicationRunner, DisposableBean {

    public static final String MATCHING_ENGINE_ORDERS_GROUP = "MATCHING_ENGINE_ORDERS_GROUP";
    public static final String MATCHING_ENGINE_ORDERS_CHANNEL = "MATCHING_ENGINE_ORDERS_CHANNEL";

    @Resource
    private ThreadPoolTaskExecutor executor;
    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private StreamMessageListenerImpl streamMessageListenerImpl;

    private StreamMessageListenerContainer<String, ObjectRecord<String, OrderCommand>> listenerContainer;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, OrderCommand>>
                containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10)
                        .executor(executor)
                        .pollTimeout(Duration.ZERO)
                        .errorHandler(Throwable::printStackTrace)
                        .targetType(OrderCommand.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, OrderCommand>>
                listenerContainer =
                StreamMessageListenerContainer
                        .create(redisConnectionFactory, containerOptions);

        initializeChannelAndGroup(stringRedisTemplate.opsForStream());

        listenerContainer.receive(
                Consumer.from(MATCHING_ENGINE_ORDERS_GROUP, "CONSUMER-1"),
                StreamOffset.create(MATCHING_ENGINE_ORDERS_CHANNEL, ReadOffset.lastConsumed()),
                streamMessageListenerImpl
        );

        this.listenerContainer = listenerContainer;
        listenerContainer.start();
    }

    @Override
    public void destroy() throws Exception {
        this.listenerContainer.stop();
    }

    @Bean
    public ThreadPoolTaskExecutor executor() {
        return new ThreadPoolTaskExecutor();
    }

    private void initializeChannelAndGroup(StreamOperations<String, ?, ?> streamOperations) {
        String status = "OK";
        try {
            StreamInfo.XInfoGroups xInfoGroups = streamOperations.groups(MATCHING_ENGINE_ORDERS_CHANNEL);
            if (xInfoGroups.stream().noneMatch(xInfoGroup -> MATCHING_ENGINE_ORDERS_GROUP.equals(xInfoGroup.groupName()))) {
                status = streamOperations.createGroup(MATCHING_ENGINE_ORDERS_CHANNEL, MATCHING_ENGINE_ORDERS_GROUP);
            }
        } catch (Exception exception) {
            RecordId initialRecord = streamOperations.add(ObjectRecord.create(MATCHING_ENGINE_ORDERS_CHANNEL, "initialize record"));
            Assert.notNull(initialRecord, "cannot initialize stream with key '" + MATCHING_ENGINE_ORDERS_CHANNEL + "'");
            status = streamOperations.createGroup(MATCHING_ENGINE_ORDERS_CHANNEL, ReadOffset.from(initialRecord), MATCHING_ENGINE_ORDERS_GROUP);
        } finally {
            Assert.isTrue("OK".equals(status), "cannot create group with name '" + MATCHING_ENGINE_ORDERS_GROUP + "'");
        }
    }
}
