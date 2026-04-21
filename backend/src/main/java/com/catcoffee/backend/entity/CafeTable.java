package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cafe_table")
public class CafeTable extends BaseEntity {

    private String tableNo;
    private Integer capacity;
    private String areaName;
    private String status;
    private String remark;
}
