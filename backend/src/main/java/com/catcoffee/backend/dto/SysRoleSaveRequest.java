package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "系统角色保存请求")
public class SysRoleSaveRequest {

    private Long id;

    @Schema(description = "角色编码", example = "manager")
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "角色名称", example = "门店经理")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "权限 ID 列表")
    @NotEmpty(message = "至少选择一个权限")
    private List<Long> permissionIds;
}
