package com.health.module.resident.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("resident")
public class Resident {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String name;
    private String idCard;
    private Integer gender;
    private LocalDate birthDate;
    private String phone;
    private String address;
    private String bloodType;
    private String marriage;
    private String occupation;
    private String allergy;
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
