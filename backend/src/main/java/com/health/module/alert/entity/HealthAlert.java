package com.health.module.alert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_alert")
public class HealthAlert {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long residentId;
    private Long indicatorId;
    private String alertType;
    private String alertValue;
    private LocalDate measureDate;
    private Integer status;
    private String handleNote;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
