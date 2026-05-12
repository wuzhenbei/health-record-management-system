package com.health.module.alert.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.PageResult;
import com.health.common.Result;
import com.health.module.alert.service.HealthAlertService;
import com.health.module.alert.vo.AlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "健康预警")
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class HealthAlertController {

    private final HealthAlertService alertService;

    @Operation(summary = "分页查询预警列表")
    @GetMapping
    public Result<PageResult<AlertVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String alertType) {
        Page<AlertVO> pg = alertService.page(pageNum, pageSize, status, alertType);
        return Result.ok(PageResult.of(pg));
    }

    @Operation(summary = "标记已处理")
    @PutMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id,
                               @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("note") : null;
        alertService.handle(id, note);
        return Result.ok();
    }

    @Operation(summary = "未处理预警数量（用于角标）")
    @GetMapping("/pending-count")
    public Result<Long> pendingCount() {
        return Result.ok(alertService.countPending());
    }

    @Operation(summary = "批量扫描历史体检数据生成预警")
    @PostMapping("/batch-scan")
    public Result<String> batchScan() {
        int count = alertService.batchScanAll();
        return Result.ok("扫描完成，新生成预警 " + count + " 条");
    }
}
