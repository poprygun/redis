package io.microsamples.cache.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
@Slf4j
public class EmbededRedisTestConfiguration {
    private final redis.embedded.RedisServer redisServer;

    public EmbededRedisTestConfiguration(@Value("${spring.redis.port}") final int redisPort) throws IOException {
        this.redisServer = new redis.embedded.RedisServer(redisPort);
    }

    @PostConstruct
    public void startRedis() {
        log.info("Initializing Redis Server.");
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        log.info("Shutting down Redis Server.");
        this.redisServer.stop();
    }
}
