package com.health.module.stats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.module.chronic.entity.ChronicDisease;
import com.health.module.chronic.mapper.ChronicDiseaseMapper;
import com.health.module.followup.entity.FollowupRecord;
import com.health.module.followup.mapper.FollowupRecordMapper;
import com.health.module.followup.vo.FollowupVO;
import com.health.module.resident.entity.Resident;
import com.health.module.resident.mapper.ResidentMapper;
import com.health.module.stats.service.StatsService;
import com.health.module.stats.vo.ChartItemVO;
import com.health.module.stats.vo.StatsOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final ResidentMapper residentMapper;
    private final ChronicDiseaseMapper chronicMapper;
    private final FollowupRecordMapper followupMapper;

    @Override
    public StatsOverviewVO overview() {
        StatsOverviewVO vo = new StatsOverviewVO();
        vo.setTotalResidents(residentMapper.selectCount(null));
        vo.setTotalChronic(chronicMapper.selectCount(null));
        vo.setTotalFollowup(followupMapper.selectCount(null));

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        vo.setMonthNewResidents(residentMapper.selectCount(
                new LambdaQueryWrapper<Resident>().ge(Resident::getCreateTime, monthStart)
        ));
        return vo;
    }

    @Override
    public List<ChartItemVO> chronicByType() {
        List<ChronicDisease> list = chronicMapper.selectList(
                new LambdaQueryWrapper<ChronicDisease>().select(ChronicDisease::getDiseaseType)
        );
        return list.stream()
                .collect(Collectors.groupingBy(ChronicDisease::getDiseaseType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new ChartItemVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartItemVO> chronicByGender() {
        List<ChronicDisease> chronics = chronicMapper.selectList(
                new LambdaQueryWrapper<ChronicDisease>().select(ChronicDisease::getResidentId)
        );
        List<Long> residentIds = chronics.stream()
                .map(ChronicDisease::getResidentId).distinct().collect(Collectors.toList());
        if (residentIds.isEmpty()) return Collections.emptyList();

        List<Resident> residents = residentMapper.selectBatchIds(residentIds);
        Map<Long, Integer> genderMap = residents.stream()
                .collect(Collectors.toMap(Resident::getId, r -> r.getGender() != null ? r.getGender() : -1));

        Map<String, Long> result = new LinkedHashMap<>();
        result.put("男", 0L);
        result.put("女", 0L);
        residentIds.forEach(id -> {
            Integer g = genderMap.get(id);
            if (g != null && g == 1) result.merge("男", 1L, Long::sum);
            else if (g != null && g == 0) result.merge("女", 1L, Long::sum);
        });
        return result.entrySet().stream()
                .map(e -> new ChartItemVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartItemVO> chronicByAge() {
        List<ChronicDisease> chronics = chronicMapper.selectList(
                new LambdaQueryWrapper<ChronicDisease>().select(ChronicDisease::getResidentId)
        );
        List<Long> residentIds = chronics.stream()
                .map(ChronicDisease::getResidentId).distinct().collect(Collectors.toList());
        if (residentIds.isEmpty()) return Collections.emptyList();

        List<Resident> residents = residentMapper.selectBatchIds(residentIds);
        int currentYear = LocalDate.now().getYear();

        Map<String, Long> ageGroups = new LinkedHashMap<>();
        ageGroups.put("30岁以下", 0L);
        ageGroups.put("30-44岁", 0L);
        ageGroups.put("45-59岁", 0L);
        ageGroups.put("60-74岁", 0L);
        ageGroups.put("75岁以上", 0L);

        residents.forEach(r -> {
            if (r.getBirthDate() == null) return;
            int age = currentYear - r.getBirthDate().getYear();
            String group;
            if (age < 30) group = "30岁以下";
            else if (age < 45) group = "30-44岁";
            else if (age < 60) group = "45-59岁";
            else if (age < 75) group = "60-74岁";
            else group = "75岁以上";
            ageGroups.merge(group, 1L, Long::sum);
        });

        return ageGroups.entrySet().stream()
                .map(e -> new ChartItemVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartItemVO> followupTrend() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        List<FollowupRecord> list = followupMapper.selectList(
                new LambdaQueryWrapper<FollowupRecord>()
                        .ge(FollowupRecord::getFollowupDate, sixMonthsAgo)
                        .select(FollowupRecord::getFollowupDate));
        return buildMonthTrend(list.stream()
                .map(r -> r.getFollowupDate().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .collect(Collectors.toList()));
    }

    @Override
    public List<ChartItemVO> residentTrend() {
        LocalDateTime sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1).atStartOfDay();
        List<Resident> list = residentMapper.selectList(
                new LambdaQueryWrapper<Resident>()
                        .ge(Resident::getCreateTime, sixMonthsAgo)
                        .select(Resident::getCreateTime));
        return buildMonthTrend(list.stream()
                .map(r -> r.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .collect(Collectors.toList()));
    }

    private List<ChartItemVO> buildMonthTrend(List<String> months) {
        Map<String, Long> countMap = months.stream()
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()));
        List<ChartItemVO> result = new ArrayList<>();
        LocalDate cursor = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        for (int i = 0; i < 6; i++) {
            String key = cursor.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            result.add(new ChartItemVO(key, countMap.getOrDefault(key, 0L)));
            cursor = cursor.plusMonths(1);
        }
        return result;
    }

    @Override
    public List<FollowupVO> pendingFollowup() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(7);
        List<FollowupRecord> list = followupMapper.selectList(
                new LambdaQueryWrapper<FollowupRecord>()
                        .between(FollowupRecord::getNextDate, today, limit)
                        .orderByAsc(FollowupRecord::getNextDate));
        if (list.isEmpty()) return Collections.emptyList();

        List<Long> residentIds = list.stream().map(FollowupRecord::getResidentId)
                .distinct().collect(Collectors.toList());
        Map<Long, String> nameMap = residentMapper.selectBatchIds(residentIds).stream()
                .collect(Collectors.toMap(Resident::getId, Resident::getName));

        return list.stream().map(r -> {
            FollowupVO vo = new FollowupVO();
            vo.setId(r.getId());
            vo.setResidentId(r.getResidentId());
            vo.setResidentName(nameMap.getOrDefault(r.getResidentId(), ""));
            vo.setFollowupDate(r.getFollowupDate());
            vo.setDoctorName(r.getDoctorName());
            vo.setFollowupType(r.getFollowupType());
            vo.setNextDate(r.getNextDate());
            return vo;
        }).collect(Collectors.toList());
    }
}
