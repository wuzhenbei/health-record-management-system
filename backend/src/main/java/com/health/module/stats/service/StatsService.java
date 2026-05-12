package com.health.module.stats.service;

import com.health.module.followup.vo.FollowupVO;
import com.health.module.stats.vo.ChartItemVO;
import com.health.module.stats.vo.StatsOverviewVO;

import java.util.List;

public interface StatsService {
    StatsOverviewVO overview();
    List<ChartItemVO> chronicByType();
    List<ChartItemVO> chronicByGender();
    List<ChartItemVO> chronicByAge();
    List<ChartItemVO> followupTrend();
    List<ChartItemVO> residentTrend();
    List<FollowupVO> pendingFollowup();
}
