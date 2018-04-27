# Demo Spring Boot / Redis configurations

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

Inspiration is taken from [this example](https://github.com/michaelcgood/spring-data-redis-example).