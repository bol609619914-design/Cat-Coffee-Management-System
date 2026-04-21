package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "注册请求参数")
public class RegisterRequest {

    @Schema(description = "用户名", example = "user")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在 4 到 20 位之间")
    private String username;

    @Schema(description = "昵称", example = "普通用户")
    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过 20 位")
    private String nickname;

    @Schema(description = "密码", example = "user123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在 6 到 20 位之间")
    private String password;
}
