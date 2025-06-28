package com.example.redis_demo_my;

import com.example.redis_demo_my.configuration.properties.RedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {RedisProperties.class})
public class RedisDemoMyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisDemoMyApplication.class, args);
	}

}
