package com.health.module.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.BusinessException;
import com.health.module.chronic.entity.ChronicDisease;
import com.health.module.chronic.mapper.ChronicDiseaseMapper;
import com.health.module.followup.dto.FollowupQueryDTO;
import com.health.module.followup.entity.FollowupRecord;
import com.health.module.followup.mapper.FollowupRecordMapper;
import com.health.module.followup.service.FollowupRecordService;
import com.health.module.followup.vo.FollowupVO;
import com.health.module.resident.entity.Resident;
import com.health.module.resident.mapper.ResidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowupRecordServiceImpl implements FollowupRecordService {

    private final FollowupRecordMapper mapper;
    private final ResidentMapper residentMapper;
    private final ChronicDiseaseMapper chronicMapper;

    @Override
    public Page<FollowupVO> page(FollowupQueryDTO dto) {
        LambdaQueryWrapper<FollowupRecord> wrapper = new LambdaQueryWrapper<FollowupRecord>()
                .eq(dto.getResidentId() != null, FollowupRecord::getResidentId, dto.getResidentId())
                .eq(dto.getDoctorId() != null, FollowupRecord::getDoctorId, dto.getDoctorId())
                .like(org.springframework.util.StringUtils.hasText(dto.getDoctorName()),
                        FollowupRecord::getDoctorName, dto.getDoctorName())
                .orderByDesc(FollowupRecord::getFollowupDate);
        if (org.springframework.util.StringUtils.hasText(dto.getResidentName())) {
            List<Long> rids = residentMapper.selectList(
                    new LambdaQueryWrapper<Resident>().like(Resident::getName, dto.getResidentName()))
                    .stream().map(Resident::getId).collect(Collectors.toList());
            if (rids.isEmpty()) rids = java.util.Collections.singletonList(-1L);
            wrapper.in(FollowupRecord::getResidentId, rids);
        }

        Page<FollowupRecord> entityPage = mapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper
        );

        List<Long> residentIds = entityPage.getRecords().stream()
                .map(FollowupRecord::getResidentId).distinct().collect(Collectors.toList());
        Map<Long, Resident> residentMap = residentIds.isEmpty() ? Collections.emptyMap() :
                residentMapper.selectBatchIds(residentIds).stream()
                        .collect(Collectors.toMap(Resident::getId, r -> r));

        List<Long> chronicIds = entityPage.getRecords().stream()
                .map(FollowupRecord::getChronicId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, ChronicDisease> chronicMap = chronicIds.isEmpty() ? Collections.emptyMap() :
                chronicMapper.selectBatchIds(chronicIds).stream()
                        .collect(Collectors.toMap(ChronicDisease::getId, c -> c));

        List<FollowupVO> voList = entityPage.getRecords().stream().map(f -> {
            FollowupVO vo = new FollowupVO();
            vo.setId(f.getId());
            vo.setResidentId(f.getResidentId());
            vo.setChronicId(f.getChronicId());
            vo.setFollowupDate(f.getFollowupDate());
            vo.setDoctorId(f.getDoctorId());
            vo.setDoctorName(f.getDoctorName());
            vo.setFollowupType(f.getFollowupType());
            vo.setBp(f.getBp());
            vo.setBloodSugar(f.getBloodSugar());
            vo.setSymptom(f.getSymptom());
            vo.setAdvice(f.getAdvice());
            vo.setNextDate(f.getNextDate());
            vo.setCreateTime(f.getCreateTime());
            Resident r = residentMap.get(f.getResidentId());
            if (r != null) vo.setResidentName(r.getName());
            ChronicDisease c = f.getChronicId() != null ? chronicMap.get(f.getChronicId()) : null;
            if (c != null) vo.setDiseaseType(c.getDiseaseType());
            return vo;
        }).collect(Collectors.toList());

        Page<FollowupVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<FollowupRecord> listByResident(Long residentId) {
        return mapper.selectList(new LambdaQueryWrapper<FollowupRecord>()
                .eq(FollowupRecord::getResidentId, residentId)
                .orderByDesc(FollowupRecord::getFollowupDate));
    }

    @Override
    public void create(FollowupRecord record) {
        mapper.insert(record);
    }

    @Override
    public void update(Long id, FollowupRecord record) {
        if (mapper.selectById(id) == null) throw new BusinessException("记录不存在");
        record.setId(id);
        mapper.updateById(record);
    }

    @Override
    public void delete(Long id) {
        if (mapper.selectById(id) == null) throw new BusinessException("记录不存在");
        mapper.deleteById(id);
    }
}
