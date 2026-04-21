package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "修改本人密码请求")
public class ChangePasswordRequest {

    @Schema(description = "当前密码", example = "admin123")
    @NotBlank(message = "当前密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "newAdmin123")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
