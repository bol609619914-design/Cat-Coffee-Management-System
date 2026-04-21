package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("drink")
public class Drink extends BaseEntity {

    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer isRecommended;
    private String imageUrl;
    private String description;
    private Integer status;
}
