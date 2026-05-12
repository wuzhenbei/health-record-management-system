package com.health.module.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.common.BusinessException;
import com.health.module.medical.entity.MedicalHistory;
import com.health.module.medical.mapper.MedicalHistoryMapper;
import com.health.module.medical.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryMapper mapper;

    @Override
    public List<MedicalHistory> listByResident(Long residentId) {
        return mapper.selectList(new LambdaQueryWrapper<MedicalHistory>()
                .eq(MedicalHistory::getResidentId, residentId)
                .orderByDesc(MedicalHistory::getVisitDate));
    }

    @Override
    public void create(MedicalHistory history) {
        mapper.insert(history);
    }

    @Override
    public void update(Long id, MedicalHistory history) {
        if (mapper.selectById(id) == null) throw new BusinessException("记录不存在");
        history.setId(id);
        mapper.updateById(history);
    }

    @Override
    public void delete(Long id) {
        if (mapper.selectById(id) == null) throw new BusinessException("记录不存在");
        mapper.deleteById(id);
    }
}
