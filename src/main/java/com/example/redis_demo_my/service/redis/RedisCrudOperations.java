package com.example.redis_demo_my.service.redis;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface RedisCrudOperations<DTO_TYPE, ENTITY_TYPE> {
    String KEYS_SUFFIX = "keys";

    void putToCache(DTO_TYPE dto);
    void putAllToCache(List<DTO_TYPE> list);
    Optional<DTO_TYPE> findOne(String id);
    List<DTO_TYPE> findAll();
    void cacheEvict(String key);
    void saveWithTtl(ENTITY_TYPE entity, Duration ttl);

    Duration getCurrentTtl();

    String buildRedisKey(String id);

    void cleanExpiredKeys();
}
