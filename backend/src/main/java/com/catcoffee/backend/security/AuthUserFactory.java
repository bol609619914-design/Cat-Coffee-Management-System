package com.catcoffee.backend.security;

import com.catcoffee.backend.entity.SysUser;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthUserFactory {

    private final SysUserMapper sysUserMapper;

    public AuthUserFactory(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public AuthUser fromUserId(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        return fromSysUser(sysUser);
    }

    public AuthUser fromSysUser(SysUser sysUser) {
        List<String> roles = sysUserMapper.selectRoleCodesByUserId(sysUser.getId());
        List<String> permissions = sysUserMapper.selectPermissionCodesByUserId(sysUser.getId());
        return AuthUser.builder()
                .id(sysUser.getId())
                .username(sysUser.getUsername())
                .password(sysUser.getPassword())
                .nickname(sysUser.getNickname())
                .status(sysUser.getStatus())
                .tokenVersion(sysUser.getTokenVersion())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}
