package com.health.module.oplog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.PageResult;
import com.health.common.Result;
import com.health.module.oplog.entity.OperationLog;
import com.health.module.oplog.mapper.OperationLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {

    private final OperationLogMapper logMapper;

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public Result<PageResult<OperationLog>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreateTime);
        if (StringUtils.hasText(username)) wrapper.like(OperationLog::getUsername, username);
        if (StringUtils.hasText(module)) wrapper.like(OperationLog::getModule, module);
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(OperationLog::getCreateTime,
                    LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(OperationLog::getCreateTime,
                    LocalDate.parse(endDate).atTime(23, 59, 59));
        }

        Page<OperationLog> pg = logMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.ok(PageResult.of(pg));
    }
}
