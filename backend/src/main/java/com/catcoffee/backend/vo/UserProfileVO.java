package com.catcoffee.backend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "当前登录用户信息")
public class UserProfileVO {

    @Schema(description = "用户 ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "会员积分")
    private Integer memberPoints;
    @Schema(description = "角色编码列表")
    private List<String> roles;
    @Schema(description = "权限编码列表")
    private List<String> permissions;
}
