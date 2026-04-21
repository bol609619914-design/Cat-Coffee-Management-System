package com.catcoffee.backend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统角色视图对象")
public class SysRoleVO {

    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
    private List<Long> permissionIds;
    private List<String> permissionNames;
}
