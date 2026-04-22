package com.catcoffee.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MarketingActivitySaveRequest {

    private Long id;

    @NotBlank(message = "活动名称不能为空")
    private String name;

    @NotBlank(message = "活动类型不能为空")
    private String activityType;

    private String bannerImage;

    @NotNull(message = "活动开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "活动结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "活动状态不能为空")
    private Integer status;

    private String description;

    @Valid
    private List<MarketingActivityRuleRequest> rules;
}
