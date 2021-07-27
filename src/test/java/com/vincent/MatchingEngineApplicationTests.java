package com.vincent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MatchingEngineApplicationTests {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void redisTemplateTest() {
        Map<String, String> map = new HashMap<>();
        map.put("Hello", "Vincent");
        redisTemplate.opsForValue().set("S1", map);
        redisTemplate.opsForHash().put("H1", "K1", map);
        System.out.println(redisTemplate.opsForValue().get("S1"));
        System.out.println(redisTemplate.opsForHash().get("H1", "K1"));


        MapRecord<String, String, String> entries = Record.of(map).withStreamKey("channel:stream:key1");
        RecordId recordId = redisTemplate.opsForStream().add(entries);
        System.out.println(recordId);
    }

}
