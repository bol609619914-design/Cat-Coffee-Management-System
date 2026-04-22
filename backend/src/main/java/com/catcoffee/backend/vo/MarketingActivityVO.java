package com.catcoffee.backend.vo;

import com.catcoffee.backend.entity.MarketingActivityRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketingActivityVO {

    private Long id;
    private String name;
    private String activityType;
    private String bannerImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String description;
    private List<MarketingActivityRule> rules;
}
