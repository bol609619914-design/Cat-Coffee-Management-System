package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reservation")
public class Reservation extends BaseEntity {

    private String customerName;
    private String customerPhone;
    private Integer guestCount;
    private LocalDateTime reservationTime;
    private Long tableId;
    private String status;
    private String note;
}
