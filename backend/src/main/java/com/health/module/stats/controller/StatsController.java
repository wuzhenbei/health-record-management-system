package com.health.module.stats.controller;

import com.health.common.Result;
import com.health.module.followup.vo.FollowupVO;
import com.health.module.stats.service.StatsService;
import com.health.module.stats.vo.ChartItemVO;
import com.health.module.stats.vo.StatsOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "统计分析")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "概览数据卡片")
    @GetMapping("/overview")
    public Result<StatsOverviewVO> overview() {
        return Result.ok(statsService.overview());
    }

    @Operation(summary = "慢病病种分布")
    @GetMapping("/chronic-by-type")
    public Result<List<ChartItemVO>> chronicByType() {
        return Result.ok(statsService.chronicByType());
    }

    @Operation(summary = "慢病性别分布")
    @GetMapping("/chronic-by-gender")
    public Result<List<ChartItemVO>> chronicByGender() {
        return Result.ok(statsService.chronicByGender());
    }

    @Operation(summary = "慢病年龄段分布")
    @GetMapping("/chronic-by-age")
    public Result<List<ChartItemVO>> chronicByAge() {
        return Result.ok(statsService.chronicByAge());
    }

    @Operation(summary = "近6个月随访趋势")
    @GetMapping("/followup-trend")
    public Result<List<ChartItemVO>> followupTrend() {
        return Result.ok(statsService.followupTrend());
    }

    @Operation(summary = "近6个月居民新增趋势")
    @GetMapping("/resident-trend")
    public Result<List<ChartItemVO>> residentTrend() {
        return Result.ok(statsService.residentTrend());
    }

    @Operation(summary = "7天内到期随访提醒")
    @GetMapping("/pending-followup")
    public Result<List<FollowupVO>> pendingFollowup() {
        return Result.ok(statsService.pendingFollowup());
    }
}
