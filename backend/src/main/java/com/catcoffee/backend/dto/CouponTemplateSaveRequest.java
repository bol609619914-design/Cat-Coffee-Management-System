package com.catcoffee.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponTemplateSaveRequest {

    private Long id;

    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "满减门槛不能为空")
    private BigDecimal thresholdAmount;

    @NotNull(message = "优惠金额不能为空")
    private BigDecimal discountAmount;

    @NotNull(message = "积分成本不能为空")
    @Min(value = 0, message = "积分成本不能小于 0")
    private Integer pointCost;

    @NotNull(message = "发放数量不能为空")
    @Min(value = 1, message = "发放数量至少为 1")
    private Integer totalCount;

    @NotBlank(message = "有效期类型不能为空")
    private String validityType;

    private Integer validDays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
