package com.catcoffee.backend.dto;

import lombok.Data;

@Data
public class MarketingActivityRuleRequest {

    private Long id;
    private String ruleType;
    private String ruleValue;
    private String targetType;
    private Long targetId;
    private Integer sortOrder;
}
