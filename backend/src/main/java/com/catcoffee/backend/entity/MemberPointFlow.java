package com.catcoffee.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_point_flow")
public class MemberPointFlow extends BaseEntity {

    private Long userId;
    private String changeType;
    private Integer changeAmount;
    private Integer balanceAfter;
    private String bizType;
    private Long bizId;
    private String remark;
}
