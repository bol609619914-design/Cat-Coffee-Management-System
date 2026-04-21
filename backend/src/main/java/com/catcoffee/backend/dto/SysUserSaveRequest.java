package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "系统用户保存请求")
public class SysUserSaveRequest {

    @Schema(description = "用户 ID")
    private Long id;

    @Schema(description = "用户名", example = "new_admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码，编辑时可为空表示不修改", example = "123456")
    private String password;

    @Schema(description = "昵称", example = "门店经理")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "状态 1启用 0禁用", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "角色 ID 列表")
    @NotEmpty(message = "至少选择一个角色")
    private List<Long> roleIds;
}
