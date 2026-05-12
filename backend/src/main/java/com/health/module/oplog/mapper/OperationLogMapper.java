package com.health.module.oplog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.module.oplog.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
