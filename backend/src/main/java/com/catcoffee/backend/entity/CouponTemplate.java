package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coupon_template")
public class CouponTemplate extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private Integer pointCost;
    private Integer totalCount;
    private Integer issuedCount;
    private String validityType;
    private Integer validDays;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}
