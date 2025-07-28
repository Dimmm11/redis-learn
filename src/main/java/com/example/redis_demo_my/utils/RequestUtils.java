package com.example.redis_demo_my.utils;

import java.io.BufferedReader;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RequestUtils {
    public static String extractFieldValue(HttpServletRequest request, String fieldName, ObjectMapper objectMapper) {
        try (BufferedReader reader = request.getReader()) {
            String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            if (body != null && !body.isEmpty()) {
                JsonNode node = objectMapper.readTree(body);
                JsonNode tokenNode = node.get(fieldName);
                if (tokenNode != null && !tokenNode.isNull()) {
                    return tokenNode.asText();
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract access_token from request body: {}", e.getMessage());
        }
        return null;
    }

    /**
     * private String extractAccessToken(HttpServletRequest request,
     * Supplier<String> fieldName) {
     * try (BufferedReader reader = request.getReader()) {
     * String body =
     * reader.lines().collect(Collectors.joining(System.lineSeparator()));
     * if (body != null && !body.isEmpty()) {
     * JsonNode node = objectMapper.readTree(body);
     * JsonNode tokenNode = node.get(fieldName.get());
     * if (tokenNode != null && !tokenNode.isNull()) {
     * return tokenNode.asText();
     * }
     * }
     * } catch (Exception e) {
     * log.error("Failed to extract access_token from request body: {}",
     * e.getMessage());
     * }
     * return null;
     * }
     */
}
