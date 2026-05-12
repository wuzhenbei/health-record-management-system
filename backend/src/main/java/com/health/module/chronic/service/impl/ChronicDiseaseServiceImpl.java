package com.health.module.chronic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.BusinessException;
import com.health.module.chronic.dto.ChronicQueryDTO;
import com.health.module.chronic.entity.ChronicDisease;
import com.health.module.chronic.mapper.ChronicDiseaseMapper;
import com.health.module.chronic.service.ChronicDiseaseService;
import com.health.module.chronic.vo.ChronicVO;
import com.health.module.resident.entity.Resident;
import com.health.module.resident.mapper.ResidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChronicDiseaseServiceImpl implements ChronicDiseaseService {

    private final ChronicDiseaseMapper mapper;
    private final ResidentMapper residentMapper;

    @Override
    public Page<ChronicVO> page(ChronicQueryDTO dto) {
        LambdaQueryWrapper<ChronicDisease> wrapper = new LambdaQueryWrapper<ChronicDisease>()
                .eq(StringUtils.hasText(dto.getDiseaseType()), ChronicDisease::getDiseaseType, dto.getDiseaseType())
                .eq(StringUtils.hasText(dto.getStatus()), ChronicDisease::getStatus, dto.getStatus())
                .orderByDesc(ChronicDisease::getCreateTime);
        if (StringUtils.hasText(dto.getResidentName())) {
            List<Long> rids = residentMapper.selectList(
                    new LambdaQueryWrapper<Resident>().like(Resident::getName, dto.getResidentName()))
                    .stream().map(Resident::getId).collect(Collectors.toList());
            if (rids.isEmpty()) rids = java.util.Collections.singletonList(-1L);
            wrapper.in(ChronicDisease::getResidentId, rids);
        }
        Page<ChronicDisease> entityPage = mapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        List<Long> residentIds = entityPage.getRecords().stream()
                .map(ChronicDisease::getResidentId).distinct().collect(Collectors.toList());
        Map<Long, Resident> residentMap = residentIds.isEmpty() ? java.util.Collections.emptyMap() :
                residentMapper.selectBatchIds(residentIds).stream()
                        .collect(Collectors.toMap(Resident::getId, r -> r));

        List<ChronicVO> voList = entityPage.getRecords().stream().map(c -> {
            ChronicVO vo = new ChronicVO();
            vo.setId(c.getId());
            vo.setResidentId(c.getResidentId());
            vo.setDiseaseType(c.getDiseaseType());
            vo.setConfirmDate(c.getConfirmDate());
            vo.setStatus(c.getStatus());
            vo.setLevel(c.getLevel());
            vo.setRemark(c.getRemark());
            vo.setCreateTime(c.getCreateTime());
            Resident r = residentMap.get(c.getResidentId());
            if (r != null) {
                vo.setResidentName(r.getName());
                vo.setIdCard(r.getIdCard());
            }
            return vo;
        }).collect(Collectors.toList());

        Page<ChronicVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<ChronicDisease> listByResident(Long residentId) {
        return mapper.selectList(new LambdaQueryWrapper<ChronicDisease>()
                .eq(ChronicDisease::getResidentId, residentId)
                .orderByDesc(ChronicDisease::getConfirmDate));
    }

    @Override
    public void create(ChronicDisease chronic) {
        mapper.insert(chronic);
    }

    @Override
    public void update(Long id, ChronicDisease chronic) {
        if (mapper.selectById(id) == null) throw new BusinessException("记录不存在");
        chronic.setId(id);
        mapper.updateById(chronic);
    }

    @Override
    public void delete(Long id) {
        if (mapper.selectById(id) == null) throw new BusinessException("记录不存在");
        mapper.deleteById(id);
    }
}
