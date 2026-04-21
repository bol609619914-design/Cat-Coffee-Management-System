package com.catcoffee.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateRequest {

    private Long id;

    private String customerName;

    @NotBlank(message = "手机号不能为空")
    private String customerPhone;

    @NotNull(message = "到店人数不能为空")
    @Min(value = 1, message = "到店人数至少为 1")
    private Integer guestCount;

    @NotNull(message = "预约时间不能为空")
    @Future(message = "预约时间必须大于当前时间")
    private LocalDateTime reservationTime;

    @NotNull(message = "桌台不能为空")
    private Long tableId;

    @NotBlank(message = "预约状态不能为空")
    private String status;

    private String note;
}
