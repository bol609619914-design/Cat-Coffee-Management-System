package com.catcoffee.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catcoffee.backend.entity.SysRole;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT permission_id
            FROM sys_role_permission
            WHERE role_id = #{roleId} AND deleted = 0
            """)
    List<Long> selectPermissionIdsByRoleId(Long roleId);

    @Select("""
            SELECT p.permission_name
            FROM sys_permission p
            INNER JOIN sys_role_permission rp ON rp.permission_id = p.id AND rp.deleted = 0
            WHERE rp.role_id = #{roleId} AND p.deleted = 0
            """)
    List<String> selectPermissionNamesByRoleId(Long roleId);
}
