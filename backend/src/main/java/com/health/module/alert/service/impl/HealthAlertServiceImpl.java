package com.health.module.alert.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.module.alert.entity.HealthAlert;
import com.health.module.alert.mapper.HealthAlertMapper;
import com.health.module.alert.service.HealthAlertService;
import com.health.module.alert.vo.AlertVO;
import com.health.module.health.entity.HealthIndicator;
import com.health.module.health.mapper.HealthIndicatorMapper;
import com.health.module.resident.entity.Resident;
import com.health.module.resident.mapper.ResidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthAlertServiceImpl implements HealthAlertService {

    private final HealthAlertMapper alertMapper;
    private final ResidentMapper residentMapper;
    private final HealthIndicatorMapper indicatorMapper;

    @Override
    public void checkAndSave(HealthIndicator h) {
        buildAlerts(h).forEach(alertMapper::insert);
    }

    private HealthAlert build(HealthIndicator h, String type, String value) {
        HealthAlert a = new HealthAlert();
        a.setResidentId(h.getResidentId());
        a.setIndicatorId(h.getId());
        a.setAlertType(type);
        a.setAlertValue(value);
        a.setMeasureDate(h.getMeasureDate());
        a.setStatus(0);
        return a;
    }

    @Override
    public Page<AlertVO> page(int pageNum, int pageSize, Integer status, String alertType) {
        LambdaQueryWrapper<HealthAlert> wrapper = new LambdaQueryWrapper<HealthAlert>()
                .orderByDesc(HealthAlert::getCreateTime);
        if (status != null) wrapper.eq(HealthAlert::getStatus, status);
        if (StringUtils.hasText(alertType)) wrapper.eq(HealthAlert::getAlertType, alertType);

        Page<HealthAlert> pg = alertMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<Long> residentIds = pg.getRecords().stream()
                .map(HealthAlert::getResidentId).distinct().collect(Collectors.toList());
        Map<Long, Resident> residentMap = residentIds.isEmpty() ? Collections.emptyMap() :
                residentMapper.selectBatchIds(residentIds).stream()
                        .collect(Collectors.toMap(Resident::getId, r -> r));

        Page<AlertVO> result = new Page<>(pg.getCurrent(), pg.getSize(), pg.getTotal());
        result.setRecords(pg.getRecords().stream().map(a -> {
            AlertVO vo = new AlertVO();
            vo.setId(a.getId());
            vo.setResidentId(a.getResidentId());
            vo.setAlertType(a.getAlertType());
            vo.setAlertValue(a.getAlertValue());
            vo.setMeasureDate(a.getMeasureDate());
            vo.setStatus(a.getStatus());
            vo.setHandleNote(a.getHandleNote());
            vo.setCreateTime(a.getCreateTime());
            Resident r = residentMap.get(a.getResidentId());
            if (r != null) {
                vo.setResidentName(r.getName());
                vo.setIdCard(r.getIdCard());
            }
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void handle(Long id, String note) {
        HealthAlert alert = alertMapper.selectById(id);
        if (alert != null) {
            alert.setStatus(1);
            alert.setHandleNote(note);
            alertMapper.updateById(alert);
        }
    }

    @Override
    public Long countPending() {
        return alertMapper.selectCount(
                new LambdaQueryWrapper<HealthAlert>().eq(HealthAlert::getStatus, 0));
    }

    @Override
    public int batchScanAll() {
        List<HealthIndicator> all = indicatorMapper.selectList(
                new LambdaQueryWrapper<HealthIndicator>().orderByAsc(HealthIndicator::getId));
        Set<Long> existIds = alertMapper.selectList(
                new LambdaQueryWrapper<HealthAlert>()
                        .isNotNull(HealthAlert::getIndicatorId)
                        .select(HealthAlert::getIndicatorId))
                .stream().map(HealthAlert::getIndicatorId)
                .collect(java.util.stream.Collectors.toSet());
        int count = 0;
        for (HealthIndicator h : all) {
            if (h.getId() != null && existIds.contains(h.getId())) continue;
            List<HealthAlert> alerts = buildAlerts(h);
            alerts.forEach(alertMapper::insert);
            count += alerts.size();
        }
        return count;
    }

    private List<HealthAlert> buildAlerts(HealthIndicator h) {
        List<HealthAlert> alerts = new ArrayList<>();
        if (h.getSystolic() != null && h.getDiastolic() != null) {
            if (h.getSystolic() >= 140 || h.getDiastolic() >= 90) {
                alerts.add(build(h, "高血压",
                        String.format("收缩压 %d / 舒张压 %d mmHg", h.getSystolic(), h.getDiastolic())));
            }
        }
        if (h.getBloodSugar() != null && h.getBloodSugar().compareTo(new BigDecimal("7.0")) >= 0) {
            alerts.add(build(h, "高血糖", String.format("空腹血糖 %s mmol/L", h.getBloodSugar())));
        }
        if (h.getBmi() != null && h.getBmi().compareTo(new BigDecimal("28")) >= 0) {
            alerts.add(build(h, "超重", String.format("BMI %.1f", h.getBmi().doubleValue())));
        }
        if (h.getHeartRate() != null && (h.getHeartRate() < 50 || h.getHeartRate() > 110)) {
            alerts.add(build(h, "心率异常", String.format("心率 %d 次/分", h.getHeartRate())));
        }
        return alerts;
    }
}
