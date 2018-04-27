package io.microsamples.cache.redis;

import io.microsamples.cache.redis.service.DaysService;
import io.microsamples.cache.redis.service.MonthsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@Slf4j
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @RestController
    public class RedisApi {

        private final static String CACHE_SUFFIX = "~keys";

        private MonthsService monthsService;

        private DaysService daysService;

        @Autowired
        private RedisTemplate<String, Object> redisTemplate;

        @Autowired
        @Qualifier("keyRedisTemplate")
        private RedisTemplate keyRedisTemplate;

        @Autowired
        private CacheManager cacheManager;

        public RedisApi(MonthsService monthsService, DaysService daysService) {
            this.monthsService = monthsService;
            this.daysService = daysService;
        }

        @GetMapping("/months")
        public ResponseEntity<List<String>> months() {
            return new ResponseEntity<>(monthsService.springMonths(), HttpStatus.OK);
        }

        @GetMapping("/days")
        public ResponseEntity<List<String>> days() {
            return new ResponseEntity<>(daysService.daysOfWeek(), HttpStatus.OK);
        }

        @GetMapping("/cached")
        public ResponseEntity<Map> index(@RequestParam String cacheName) {

            if (StringUtils.isEmpty(cacheName))
                return new ResponseEntity("Cache name needs to be provided.", HttpStatus.BAD_REQUEST);

            Set<String> range = namedCacheValues(cacheName);

            Map<String, String> cachedData = new HashMap<>();

            if (!CollectionUtils.isEmpty(range))
                range.stream().forEach(v -> cachedData.put(v, redisTemplate.opsForValue().get(v).toString()));

            return new ResponseEntity<>(cachedData, HttpStatus.OK);
        }

        private Set<String> namedCacheValues(@RequestParam String cacheName) {
            String keyName = cacheName.concat(CACHE_SUFFIX);
            long cachedSize = keyRedisTemplate.opsForZSet().size(keyName);
            return keyRedisTemplate.opsForZSet().range(keyName, 0, cachedSize);
        }


        @GetMapping("/purge")
        public ResponseEntity<Long> purge(@RequestParam String cacheName) {

            if (StringUtils.isEmpty(cacheName))
                return new ResponseEntity("Cache name needs to be provided.", HttpStatus.BAD_REQUEST);

            Cache cache = cacheManager.getCache(cacheName);

            Set<String> range = namedCacheValues(cacheName);

            if (!CollectionUtils.isEmpty(range))
                range.stream().forEach(v -> cache.evict(v));

            return new ResponseEntity(range.size(), HttpStatus.OK);
        }

    }
}
