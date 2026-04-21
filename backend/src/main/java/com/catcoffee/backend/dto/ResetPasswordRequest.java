package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "管理员重置密码请求")
public class ResetPasswordRequest {

    @Schema(description = "新密码", example = "reset123")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
