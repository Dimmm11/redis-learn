package com.example.redis_demo_my.service;

import java.util.Optional;

public interface CrudOperations<T> {

    T putToCache(T t);

    Optional<T> getById(String id);
    void cacheEvict(String key);
}
