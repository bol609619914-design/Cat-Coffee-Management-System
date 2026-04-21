package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.SysPermissionSaveRequest;
import com.catcoffee.backend.dto.ResetPasswordRequest;
import com.catcoffee.backend.dto.SysRoleSaveRequest;
import com.catcoffee.backend.dto.SysUserSaveRequest;
import com.catcoffee.backend.entity.SysPermission;
import com.catcoffee.backend.service.SystemService;
import com.catcoffee.backend.vo.SysRoleVO;
import com.catcoffee.backend.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Tag(name = "系统管理")
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('system:user:read')")
    @Operation(summary = "分页查询用户")
    public ApiResponse<PageResult<SysUserVO>> users(@RequestParam(defaultValue = "1") @Parameter(description = "当前页") Long current,
                                                    @RequestParam(defaultValue = "10") @Parameter(description = "每页条数") Long size,
                                                    @RequestParam(required = false) @Parameter(description = "关键词") String keyword,
                                                    @RequestParam(required = false) @Parameter(description = "状态") Integer status) {
        return ApiResponse.success(systemService.listUsers(current, size, keyword, status));
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('system:user:write')")
    @Operation(summary = "新增或编辑用户")
    public ApiResponse<SysUserVO> saveUser(@Valid @RequestBody SysUserSaveRequest request) {
        return ApiResponse.success("用户保存成功", systemService.saveUser(request));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @Operation(summary = "删除用户")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        systemService.deleteUser(id);
        return ApiResponse.success("用户删除成功", null);
    }

    @PostMapping("/users/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:write')")
    @Operation(summary = "重置用户密码")
    public ApiResponse<Void> resetUserPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        systemService.resetUserPassword(id, request);
        return ApiResponse.success("密码重置成功", null);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('system:role:read')")
    @Operation(summary = "分页查询角色")
    public ApiResponse<PageResult<SysRoleVO>> roles(@RequestParam(defaultValue = "1") Long current,
                                                    @RequestParam(defaultValue = "10") Long size,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Integer status) {
        return ApiResponse.success(systemService.listRoles(current, size, keyword, status));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('system:role:write')")
    @Operation(summary = "新增或编辑角色")
    public ApiResponse<SysRoleVO> saveRole(@Valid @RequestBody SysRoleSaveRequest request) {
        return ApiResponse.success("角色保存成功", systemService.saveRole(request));
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @Operation(summary = "删除角色")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        systemService.deleteRole(id);
        return ApiResponse.success("角色删除成功", null);
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('system:permission:read')")
    @Operation(summary = "分页查询权限")
    public ApiResponse<PageResult<SysPermission>> permissions(@RequestParam(defaultValue = "1") Long current,
                                                              @RequestParam(defaultValue = "10") Long size,
                                                              @RequestParam(required = false) String keyword,
                                                              @RequestParam(required = false) Integer status) {
        return ApiResponse.success(systemService.listPermissions(current, size, keyword, status));
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('system:permission:write')")
    @Operation(summary = "新增或编辑权限")
    public ApiResponse<SysPermission> savePermission(@Valid @RequestBody SysPermissionSaveRequest request) {
        return ApiResponse.success("权限保存成功", systemService.savePermission(request));
    }

    @DeleteMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    @Operation(summary = "删除权限")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        systemService.deletePermission(id);
        return ApiResponse.success("权限删除成功", null);
    }
}
