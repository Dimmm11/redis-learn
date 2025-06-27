package com.example.redis_demo_my.service;

public interface CacheCrudOperations<T> {

    T putToCache(T t);
    void cacheEvict(String key);
}
