package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_coupon")
public class UserCoupon extends BaseEntity {

    private Long userId;
    private Long templateId;
    private String couponName;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private String status;
    private LocalDateTime receivedTime;
    private LocalDateTime usedTime;
    private LocalDateTime expireTime;
    private Long orderId;
    private String sourceType;
}
