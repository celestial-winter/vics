package com.infinityworks.webapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

import static net.logstash.logback.marker.Markers.append;

@Service
public class ActiveSessionsTask {
    private static final String SESSION_KEY_PREFIX = "spring:session:sessions*";
    private final Logger log = LoggerFactory.getLogger(ActiveSessionsTask.class);
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ActiveSessionsTask(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 1000 * 60 * 2)
    public void countActiveSessions() {
        Set<String> keys = redisTemplate.keys(SESSION_KEY_PREFIX);
        String msg = "Active user count=" + keys.size();
        log.info(append("active_users", keys.size()), msg);
    }
}
