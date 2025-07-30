package com.example.redis_demo_my.configuration.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.oauth2")
@NoArgsConstructor
public class GithubClientProperties implements OauthClientProperties {
    private String clientId;
    private String clientSecret;
}
