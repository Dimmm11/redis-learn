package com.example.redis_demo_my.model.transformers;

public interface Transformer<T,B> {

    B transform(T t);
}
