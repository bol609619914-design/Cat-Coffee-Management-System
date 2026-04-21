package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.dto.ChangePasswordRequest;
import com.catcoffee.backend.dto.LoginRequest;
import com.catcoffee.backend.dto.RefreshTokenRequest;
import com.catcoffee.backend.dto.RegisterRequest;
import com.catcoffee.backend.service.AuthService;
import com.catcoffee.backend.vo.LoginVO;
import com.catcoffee.backend.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证中心")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "账号登录")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("登录成功", authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新访问令牌")
    public ApiResponse<LoginVO> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success("刷新成功", authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success("退出成功", null);
    }

    @PostMapping("/password/change")
    @Operation(summary = "修改本人密码")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("密码修改成功，请重新登录", null);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户")
    public ApiResponse<UserProfileVO> me() {
        return ApiResponse.success(authService.currentUser());
    }
}
