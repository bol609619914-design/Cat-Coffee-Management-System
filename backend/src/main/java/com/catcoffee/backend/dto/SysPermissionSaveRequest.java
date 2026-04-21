package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "系统权限保存请求")
public class SysPermissionSaveRequest {

    private Long id;

    @Schema(description = "权限编码", example = "dashboard:view")
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @Schema(description = "权限名称", example = "查看看板")
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @Schema(description = "所属模块", example = "经营看板")
    @NotBlank(message = "模块名称不能为空")
    private String moduleName;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
