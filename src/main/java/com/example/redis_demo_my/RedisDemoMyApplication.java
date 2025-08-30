package com.example.redis_demo_my;

import com.example.redis_demo_my.configuration.properties.GithubClientProperties;
import com.example.redis_demo_my.configuration.properties.JwtProperties;
import com.example.redis_demo_my.configuration.properties.KafkaProperties;
import com.example.redis_demo_my.configuration.redis.properties.RedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(value = {RedisProperties.class, JwtProperties.class, GithubClientProperties.class, KafkaProperties.class})
@ConfigurationPropertiesScan(basePackages = "com.example.redis_demo_my.configuration.properties")
@EnableCaching
public class RedisDemoMyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisDemoMyApplication.class, args);
	}

}
