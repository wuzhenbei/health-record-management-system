package com.health.module.resident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.BusinessException;
import com.health.module.resident.dto.ResidentQueryDTO;
import com.health.module.resident.entity.Resident;
import com.health.module.resident.mapper.ResidentMapper;
import com.health.module.resident.service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ResidentServiceImpl implements ResidentService {

    private final ResidentMapper residentMapper;

    @Override
    public Page<Resident> page(ResidentQueryDTO dto) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<Resident>()
                .like(StringUtils.hasText(dto.getName()), Resident::getName, dto.getName())
                .like(StringUtils.hasText(dto.getIdCard()), Resident::getIdCard, dto.getIdCard())
                .like(StringUtils.hasText(dto.getPhone()), Resident::getPhone, dto.getPhone())
                .eq(dto.getGender() != null, Resident::getGender, dto.getGender())
                .orderByDesc(Resident::getCreateTime);
        return residentMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public Resident getById(Long id) {
        Resident r = residentMapper.selectById(id);
        if (r == null) throw new BusinessException("居民档案不存在");
        return r;
    }

    @Override
    public Resident getByUserId(Long userId) {
        return residentMapper.selectOne(
                new LambdaQueryWrapper<Resident>().eq(Resident::getUserId, userId)
        );
    }

    @Override
    public void create(Resident resident) {
        if (StringUtils.hasText(resident.getIdCard())) {
            Long count = residentMapper.selectCount(
                    new LambdaQueryWrapper<Resident>().eq(Resident::getIdCard, resident.getIdCard())
            );
            if (count > 0) throw new BusinessException("身份证号已存在");
        }
        residentMapper.insert(resident);
    }

    @Override
    public void update(Long id, Resident resident) {
        Resident existing = getById(id);
        resident.setId(existing.getId());
        residentMapper.updateById(resident);
    }

    @Override
    public void delete(Long id) {
        if (residentMapper.selectById(id) == null) throw new BusinessException("居民档案不存在");
        residentMapper.deleteById(id);
    }
}
