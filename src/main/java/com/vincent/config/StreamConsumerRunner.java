package com.vincent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;

@Component
public class StreamConsumerRunner implements ApplicationRunner, DisposableBean {

    public static final String MAIL_CHANNEL = "CHANNEL:STREAM:MAIL";
    public static final String MAIL_GROUP = "GROUP:MAIL";

    private final ThreadPoolTaskExecutor executor;
    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate stringRedisTemplate;

    private StreamMessageListenerContainer<String, ObjectRecord<String, Object>> listenerContainer;

    public StreamConsumerRunner(ThreadPoolTaskExecutor executor, RedisConnectionFactory redisConnectionFactory, StringRedisTemplate stringRedisTemplate) {
        this.executor = executor;
        this.redisConnectionFactory = redisConnectionFactory;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, Object>>
                containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10)
                        .executor(executor)
                        .pollTimeout(Duration.ZERO)
                        .targetType(Object.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, Object>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);

        prepareChannelAndGroup(stringRedisTemplate.opsForStream(), MAIL_CHANNEL, MAIL_GROUP);

        listenerContainer.receive(
                Consumer.from(MAIL_GROUP, "CONSUMER-1"),
                StreamOffset.create(MAIL_CHANNEL, ReadOffset.lastConsumed()),
                new StreamMessageListener(stringRedisTemplate)
        );

        this.listenerContainer = listenerContainer;
        this.listenerContainer.start();

    }

    @Override
    public void destroy() throws Exception {

    }

    private void prepareChannelAndGroup(StreamOperations<String, ?, ?> streamOperations, String channel, String group) {
        String status = "OK";
        try {
            StreamInfo.XInfoGroups groups = streamOperations.groups(channel);
            if (groups.stream().noneMatch(xInfoGroup -> group.equals(xInfoGroup.groupName()))) {
                status = streamOperations.createGroup(channel, group);
            }
        } catch (Exception exception) {
            RecordId initialRecord = streamOperations.add(ObjectRecord.create(channel, "Initial Record"));
            Assert.notNull(initialRecord, "Cannot initialize stream with key '" + channel + "'");
            status = streamOperations.createGroup(channel, ReadOffset.from(initialRecord), group);
        } finally {
            Assert.isTrue("OK".equals(status), "Cannot create group with name '" + group + "'");
        }
    }

    @Slf4j
    public static class StreamMessageListener implements StreamListener<String, ObjectRecord<String, Object>> {

        private final StringRedisTemplate stringRedisTemplate;

        public StreamMessageListener(StringRedisTemplate stringRedisTemplate) {
            this.stringRedisTemplate = stringRedisTemplate;
        }

        @Override
        public void onMessage(ObjectRecord<String, Object> message) {
            RecordId id = message.getId();
            Object messageValue = message.getValue();

            log.info("消费 stream:{} 中的信息: {}, 消息id:{}", message.getStream(), messageValue, id);

            stringRedisTemplate.opsForStream().acknowledge(MAIL_GROUP, message);
        }
    }

}
