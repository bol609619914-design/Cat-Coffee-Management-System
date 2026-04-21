package com.catcoffee.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catcoffee.backend.entity.SysUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("""
            SELECT id, username, password, nickname, status, token_version, create_time, update_time, deleted
            FROM sys_user
            WHERE username = #{username} AND deleted = 0
            LIMIT 1
            """)
    SysUser selectByUsername(String username);

    @Select("""
            SELECT r.role_code
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id AND ur.deleted = 0
            WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1
            """)
    List<String> selectRoleCodesByUserId(Long userId);

    @Select("""
            SELECT DISTINCT p.permission_code
            FROM sys_permission p
            INNER JOIN sys_role_permission rp ON rp.permission_id = p.id AND rp.deleted = 0
            INNER JOIN sys_user_role ur ON ur.role_id = rp.role_id AND ur.deleted = 0
            INNER JOIN sys_role r ON r.id = ur.role_id AND r.deleted = 0 AND r.status = 1
            WHERE ur.user_id = #{userId} AND p.deleted = 0 AND p.status = 1
            """)
    List<String> selectPermissionCodesByUserId(Long userId);

    @Select("""
            SELECT role_id
            FROM sys_user_role
            WHERE user_id = #{userId} AND deleted = 0
            """)
    List<Long> selectRoleIdsByUserId(Long userId);

    @Select("""
            SELECT r.role_name
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id AND ur.deleted = 0
            WHERE ur.user_id = #{userId} AND r.deleted = 0
            """)
    List<String> selectRoleNamesByUserId(Long userId);

    @Select("""
            SELECT DISTINCT user_id
            FROM sys_user_role
            WHERE role_id = #{roleId} AND deleted = 0
            """)
    List<Long> selectUserIdsByRoleId(Long roleId);

    @Select("""
            SELECT DISTINCT ur.user_id
            FROM sys_user_role ur
            INNER JOIN sys_role_permission rp ON rp.role_id = ur.role_id AND rp.deleted = 0
            WHERE rp.permission_id = #{permissionId} AND ur.deleted = 0
            """)
    List<Long> selectUserIdsByPermissionId(Long permissionId);
}
