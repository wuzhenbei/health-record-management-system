package com.health.module.medical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("medical_history")
public class MedicalHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long residentId;
    private LocalDate visitDate;
    private String disease;
    private String diagnosis;
    private String treatment;
    private Long doctorId;
    private String doctorName;
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
