package com.health.module.chronic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.module.chronic.dto.ChronicQueryDTO;
import com.health.module.chronic.entity.ChronicDisease;
import com.health.module.chronic.vo.ChronicVO;

import java.util.List;

public interface ChronicDiseaseService {
    Page<ChronicVO> page(ChronicQueryDTO dto);
    List<ChronicDisease> listByResident(Long residentId);
    void create(ChronicDisease chronic);
    void update(Long id, ChronicDisease chronic);
    void delete(Long id);
}
