package com.health.module.alert.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.module.alert.vo.AlertVO;
import com.health.module.health.entity.HealthIndicator;

public interface HealthAlertService {
    void checkAndSave(HealthIndicator indicator);
    Page<AlertVO> page(int pageNum, int pageSize, Integer status, String alertType);
    void handle(Long id, String note);
    Long countPending();
    int batchScanAll();
}
