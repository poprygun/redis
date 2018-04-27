package io.microsamples.cache.redis.config;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CacheKey {
    private String className;
    private String method;
    private List<String> params;
}
