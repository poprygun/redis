package io.microsamples.cache.redis;

import io.microsamples.cache.redis.service.MonthsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @RestController
    public class RedisApi {
        private final String CACHE_NAME = "months";

        private MonthsService monthsService;

        @Autowired
        private RedisTemplate<String, Object> redisTemplate;

        @Autowired
        @Qualifier("keyRedisTemplate")
        private RedisTemplate keyRedisTemplate;

        @Autowired
        private CacheManager cacheManager;

        public RedisApi(MonthsService monthsService) {
            this.monthsService = monthsService;
        }

        @GetMapping("/")
        public ResponseEntity<List<String>> months() {
            return new ResponseEntity<>(monthsService.springMonths(), HttpStatus.OK);
        }

        @GetMapping("/cached")
        public ResponseEntity<Map> index() {
            String keyName = CACHE_NAME.concat("~keys");

            long cachedSize = keyRedisTemplate.opsForZSet().size(keyName);
            Set<String> range = keyRedisTemplate.opsForZSet().range(keyName, 0, cachedSize);

            Map<String, String> cachedData = new HashMap<>();

            for (String key : range) {
                cachedData.put(key, redisTemplate.opsForValue().get(key).toString());
            }

            return new ResponseEntity<>(cachedData, HttpStatus.OK);
        }


        @GetMapping("/purge")
        public ResponseEntity purge() {

            Cache cache = cacheManager.getCache(CACHE_NAME);
            cache.clear();

            return new ResponseEntity(HttpStatus.OK);
        }

    }
}
