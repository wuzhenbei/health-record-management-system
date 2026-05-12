package com.health.module.stats.vo;

import lombok.Data;

@Data
public class StatsOverviewVO {
    private Long totalResidents;
    private Long totalChronic;
    private Long totalFollowup;
    private Long monthNewResidents;
}
