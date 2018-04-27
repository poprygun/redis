package io.microsamples.cache.redis.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@CacheConfig(cacheNames = "days")
public class DaysService {

    @Cacheable
    public List<String> daysOfWeek(){
        String[] months = {"Monday", "Tuesday", "Wednesday", "Thursday"};
        return Arrays.asList(months);
    }
}
