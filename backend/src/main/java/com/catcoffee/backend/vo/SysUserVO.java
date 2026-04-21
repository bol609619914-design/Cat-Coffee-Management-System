package com.catcoffee.backend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统用户视图对象")
public class SysUserVO {

    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private List<Long> roleIds;
    private List<String> roleNames;
}
