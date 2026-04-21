package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cat")
public class Cat extends BaseEntity {

    private String name;
    private String breed;
    private Integer age;
    private String gender;
    private String healthStatus;
    private String personalityTag;
    private String adoptionStatus;
    private BigDecimal feedingCost;
    private LocalDate birthday;
    private String avatar;
    private String introduction;
}
