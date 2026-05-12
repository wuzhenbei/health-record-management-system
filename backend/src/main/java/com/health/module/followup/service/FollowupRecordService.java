package com.health.module.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.module.followup.dto.FollowupQueryDTO;
import com.health.module.followup.entity.FollowupRecord;
import com.health.module.followup.vo.FollowupVO;

import java.util.List;

public interface FollowupRecordService {
    Page<FollowupVO> page(FollowupQueryDTO dto);
    List<FollowupRecord> listByResident(Long residentId);
    void create(FollowupRecord record);
    void update(Long id, FollowupRecord record);
    void delete(Long id);
}
