package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.SysPermissionSaveRequest;
import com.catcoffee.backend.dto.ResetPasswordRequest;
import com.catcoffee.backend.dto.SysRoleSaveRequest;
import com.catcoffee.backend.dto.SysUserSaveRequest;
import com.catcoffee.backend.entity.SysPermission;
import com.catcoffee.backend.vo.SysRoleVO;
import com.catcoffee.backend.vo.SysUserVO;

public interface SystemService {

    PageResult<SysUserVO> listUsers(long current, long size, String keyword, Integer status);

    SysUserVO saveUser(SysUserSaveRequest request);

    void deleteUser(Long id);

    void resetUserPassword(Long id, ResetPasswordRequest request);

    PageResult<SysRoleVO> listRoles(long current, long size, String keyword, Integer status);

    SysRoleVO saveRole(SysRoleSaveRequest request);

    void deleteRole(Long id);

    PageResult<SysPermission> listPermissions(long current, long size, String keyword, Integer status);

    SysPermission savePermission(SysPermissionSaveRequest request);

    void deletePermission(Long id);
}
