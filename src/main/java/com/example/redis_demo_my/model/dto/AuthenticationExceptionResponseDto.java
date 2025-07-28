package com.example.redis_demo_my.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthenticationExceptionResponseDto implements Serializable {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
