package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_order_item")
public class CustomerOrderItem extends BaseEntity {

    private Long orderId;
    private Long drinkId;
    private String drinkName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
