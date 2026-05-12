package com.health.module.health.controller;

import com.health.common.Result;
import com.health.module.health.entity.HealthIndicator;
import com.health.module.health.service.HealthIndicatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "健康指标管理")
@RestController
@RequestMapping("/api/health-indicators")
@RequiredArgsConstructor
public class HealthIndicatorController {

    private final HealthIndicatorService service;

    @Operation(summary = "按居民ID查询健康指标列表")
    @GetMapping("/resident/{residentId}")
    public Result<List<HealthIndicator>> listByResident(@PathVariable Long residentId) {
        return Result.ok(service.listByResident(residentId));
    }

    @Operation(summary = "获取体检趋势数据（最近10条，升序）")
    @GetMapping("/resident/{residentId}/trend")
    public Result<List<HealthIndicator>> trend(@PathVariable Long residentId) {
        List<HealthIndicator> list = service.listByResident(residentId);
        int size = list.size();
        List<HealthIndicator> trend = list.subList(Math.max(0, size - 10), size);
        java.util.Collections.reverse(trend);
        return Result.ok(trend);
    }

    @Operation(summary = "新增健康指标")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> create(@RequestBody HealthIndicator indicator) {
        service.create(indicator);
        return Result.ok();
    }

    @Operation(summary = "修改健康指标")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> update(@PathVariable Long id, @RequestBody HealthIndicator indicator) {
        service.update(id, indicator);
        return Result.ok();
    }

    @Operation(summary = "删除健康指标")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
