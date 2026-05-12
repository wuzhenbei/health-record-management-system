package com.health.module.followup.dto;

import lombok.Data;

@Data
public class FollowupQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Long residentId;
    private Long doctorId;
    private String residentName;
    private String doctorName;
}
