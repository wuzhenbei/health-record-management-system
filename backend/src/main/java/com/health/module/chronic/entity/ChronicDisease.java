package com.health.module.chronic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("chronic_disease")
public class ChronicDisease {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long residentId;
    private String diseaseType;
    private LocalDate confirmDate;
    private String status;
    private String level;
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
