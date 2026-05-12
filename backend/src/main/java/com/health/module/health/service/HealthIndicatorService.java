package com.health.module.health.service;

import com.health.module.health.entity.HealthIndicator;

import java.util.List;

public interface HealthIndicatorService {
    List<HealthIndicator> listByResident(Long residentId);
    HealthIndicator getById(Long id);
    void create(HealthIndicator indicator);
    void update(Long id, HealthIndicator indicator);
    void delete(Long id);
}
