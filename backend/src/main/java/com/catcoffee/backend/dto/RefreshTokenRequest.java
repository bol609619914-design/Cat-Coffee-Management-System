package com.catcoffee.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "刷新 token 请求参数")
public class RefreshTokenRequest {

    @Schema(description = "刷新令牌")
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
