package com.health.module.resident.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.module.resident.dto.ResidentQueryDTO;
import com.health.module.resident.entity.Resident;

public interface ResidentService {

    Page<Resident> page(ResidentQueryDTO dto);

    Resident getById(Long id);

    Resident getByUserId(Long userId);

    void create(Resident resident);

    void update(Long id, Resident resident);

    void delete(Long id);
}
