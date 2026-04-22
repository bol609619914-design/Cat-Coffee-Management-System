package com.catcoffee.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponVO {

    private Long id;
    private Long userId;
    private String username;
    private String couponName;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private String status;
    private LocalDateTime receivedTime;
    private LocalDateTime usedTime;
    private LocalDateTime expireTime;
    private String sourceType;
}
