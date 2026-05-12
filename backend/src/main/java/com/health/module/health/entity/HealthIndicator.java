package com.health.module.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_indicator")
public class HealthIndicator {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long residentId;
    private LocalDate measureDate;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;
    private Integer systolic;
    private Integer diastolic;
    private Integer heartRate;
    private BigDecimal bloodSugar;
    private BigDecimal cholesterol;
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
