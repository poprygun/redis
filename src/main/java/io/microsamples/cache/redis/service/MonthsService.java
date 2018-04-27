package io.microsamples.cache.redis.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@CacheConfig(cacheNames = "months")
public class MonthsService {

    @Cacheable
    public List<String> springMonths(){
        String[] months = {"March", "April", "May", "June"};
        return Arrays.asList(months);
    }
}
