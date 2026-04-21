package com.catcoffee.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CatSaveRequest {

    private Long id;

    @NotBlank(message = "猫咪名称不能为空")
    private String name;

    @NotBlank(message = "品种不能为空")
    private String breed;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能为负数")
    private Integer age;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotBlank(message = "健康状态不能为空")
    private String healthStatus;

    private String personalityTag;
    private String adoptionStatus;
    private BigDecimal feedingCost;
    private LocalDate birthday;
    private String avatar;
    private String introduction;
}
