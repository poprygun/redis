# Demo Spring Boot / Redis configurations

## Steps to experiment
1. http://localhost:8080 Should list and cache data.
2. List cached data in http://localhost:8080/cached
3. Purge cache with http://localhost:8080/purge

`CacheConfiguration` class defines methods for Cache Manager and Key Generator.

Use of `@Cacheable` puts entries to redis that define cache keys for namespace `months~keys`
in addition to actual `@Cacheable` key.

```bash
127.0.0.1:6379> keys *
1) "months~keys"
2) "{\"className\":\"class io.microsamples.cache.redis.service.MonthsService\",\"method\":\"public java.util.List io.microsamples.cache.redis.service.MonthsService.springMonths()\",\"params\":[]}"
``` 

`CacheKey` class is used to better illustrate the functionality of @Cacheable annotated method.
It includes class Name, method name, and method parameters.

`@Cacheable` method keys in redis are of type zset, and can be returned as

```bash
127.0.0.1:6379> type "months~keys"
zset
127.0.0.1:6379> zrange months~keys 0 1
1) "{\"className\":\"class io.microsamples.cache.redis.service.MonthsService\",\"method\":\"public java.util.List io.microsamples.cache.redis.service.MonthsService.springMonths()\",\"params\":[]}"
```

Namespace key values are of different type then actual @Cacheable data, therefore,
I needed to created an extra RedisTemplate to manipulate these keys - keyRedisTemplate using `StringRedisSerializer`
vs redisTemlate, using GenericJackson2JsonRedisSerializer for values.

Inspiration is taken from [this example](https://github.com/michaelcgood/spring-data-redis-example).
