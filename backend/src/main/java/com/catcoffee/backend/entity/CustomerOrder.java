package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_order")
public class CustomerOrder extends BaseEntity {

    private Long userId;
    private String orderNo;
    private String customerName;
    private Long reservationId;
    private Long tableId;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal payableAmount;
    private String payStatus;
    private String orderStatus;
    private Integer pointsUsed;
    private Integer pointsAwarded;
    private Long userCouponId;
    private String remark;
}
