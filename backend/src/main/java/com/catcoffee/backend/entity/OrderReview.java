package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_review")
public class OrderReview extends BaseEntity {

    private Long userId;
    private Long orderId;
    private Long drinkId;
    private Integer rating;
    private String content;
    private Integer status;
}
