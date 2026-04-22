package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("marketing_activity_rule")
public class MarketingActivityRule extends BaseEntity {

    private Long activityId;
    private String ruleType;
    private String ruleValue;
    private String targetType;
    private Long targetId;
    private Integer sortOrder;
}
