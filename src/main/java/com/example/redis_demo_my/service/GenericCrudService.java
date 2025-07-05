package com.example.redis_demo_my.service;

import java.util.List;
import java.util.UUID;

public interface GenericCrudService<DTO> {
    DTO getFromDatabase(UUID id);

    List<DTO> getAllFromDatabase();

    DTO findOne(UUID id);

    List<DTO> findAll();

    DTO create(DTO dto);

    DTO update(DTO dto);

    void delete(UUID id);
}
