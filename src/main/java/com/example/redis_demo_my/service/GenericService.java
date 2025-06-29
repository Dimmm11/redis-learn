package com.example.redis_demo_my.service;

import java.util.List;
import java.util.UUID;

public interface GenericService<DTO, ENTITY> {
    DTO getFromDatabase(UUID id);
    List<DTO> getAllFromDatabase();
}
