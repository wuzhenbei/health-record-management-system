package com.health.module.followup.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("followup_record")
public class FollowupRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long residentId;
    private Long chronicId;
    private LocalDate followupDate;
    private Long doctorId;
    private String doctorName;
    private String followupType;
    private String bp;
    private BigDecimal bloodSugar;
    private String symptom;
    private String advice;
    private LocalDate nextDate;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
