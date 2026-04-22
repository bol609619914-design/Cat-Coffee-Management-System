package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("marketing_activity")
public class MarketingActivity extends BaseEntity {

    private String name;
    private String activityType;
    private String bannerImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String description;
}
