package com.catcoffee.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableSaveRequest {

    private Long id;

    @NotBlank(message = "桌号不能为空")
    private String tableNo;

    @NotNull(message = "容纳人数不能为空")
    @Min(value = 1, message = "容纳人数至少为 1")
    private Integer capacity;

    @NotBlank(message = "区域不能为空")
    private String areaName;

    @NotBlank(message = "状态不能为空")
    private String status;

    private String remark;
}
