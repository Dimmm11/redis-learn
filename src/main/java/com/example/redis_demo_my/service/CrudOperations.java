package com.example.redis_demo_my.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface CrudOperations<T> {

    void putToCache(T t);
    void putAllToCache(List<T> list);
    Optional<T> getById(String id);
    List<T> findAll();
    void cacheEvict(String key);
}
