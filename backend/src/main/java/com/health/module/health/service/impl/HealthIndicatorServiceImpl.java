package com.health.module.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.common.BusinessException;
import com.health.module.alert.service.HealthAlertService;
import com.health.module.health.entity.HealthIndicator;
import com.health.module.health.mapper.HealthIndicatorMapper;
import com.health.module.health.service.HealthIndicatorService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthIndicatorServiceImpl implements HealthIndicatorService {

    private final HealthIndicatorMapper mapper;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private HealthAlertService alertService;

    private void calcBmi(HealthIndicator h) {
        if (h.getHeight() != null && h.getWeight() != null
                && h.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = h.getHeight().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            h.setBmi(h.getWeight().divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP));
        }
    }

    @Override
    public List<HealthIndicator> listByResident(Long residentId) {
        return mapper.selectList(new LambdaQueryWrapper<HealthIndicator>()
                .eq(HealthIndicator::getResidentId, residentId)
                .orderByDesc(HealthIndicator::getMeasureDate));
    }

    @Override
    public HealthIndicator getById(Long id) {
        HealthIndicator h = mapper.selectById(id);
        if (h == null) throw new BusinessException("记录不存在");
        return h;
    }

    @Override
    public void create(HealthIndicator indicator) {
        calcBmi(indicator);
        mapper.insert(indicator);
        if (alertService != null) alertService.checkAndSave(indicator);
    }

    @Override
    public void update(Long id, HealthIndicator indicator) {
        getById(id);
        indicator.setId(id);
        calcBmi(indicator);
        mapper.updateById(indicator);
        if (alertService != null) alertService.checkAndSave(indicator);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        mapper.deleteById(id);
    }
}
