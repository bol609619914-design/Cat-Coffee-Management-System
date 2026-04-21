package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.SysPermissionSaveRequest;
import com.catcoffee.backend.dto.ResetPasswordRequest;
import com.catcoffee.backend.dto.SysRoleSaveRequest;
import com.catcoffee.backend.dto.SysUserSaveRequest;
import com.catcoffee.backend.entity.SysPermission;
import com.catcoffee.backend.entity.SysRole;
import com.catcoffee.backend.entity.SysRolePermission;
import com.catcoffee.backend.entity.SysUser;
import com.catcoffee.backend.entity.SysUserRole;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.SysPermissionMapper;
import com.catcoffee.backend.mapper.SysRoleMapper;
import com.catcoffee.backend.mapper.SysRolePermissionMapper;
import com.catcoffee.backend.mapper.SysUserMapper;
import com.catcoffee.backend.mapper.SysUserRoleMapper;
import com.catcoffee.backend.service.SystemService;
import com.catcoffee.backend.vo.SysRoleVO;
import com.catcoffee.backend.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUserVO> listUsers(long current, long size, String keyword, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), SysUser::getUsername, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), SysUser::getNickname, keyword)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(current, size), wrapper);
        List<SysUserVO> records = page.getRecords().stream().map(user -> new SysUserVO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getStatus(),
                sysUserMapper.selectRoleIdsByUserId(user.getId()),
                sysUserMapper.selectRoleNamesByUserId(user.getId())
        )).toList();
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVO saveUser(SysUserSaveRequest request) {
        SysUser user;
        boolean shouldInvalidateToken = false;
        if (request.getId() == null) {
            if (!StringUtils.hasText(request.getPassword())) {
                throw new BusinessException("新增用户时密码不能为空");
            }
            if (sysUserMapper.selectByUsername(request.getUsername()) != null) {
                throw new BusinessException("用户名已存在");
            }
            user = new SysUser();
            user.setTokenVersion(0);
        } else {
            user = sysUserMapper.selectById(request.getId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
        }
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        if (request.getId() != null && !Objects.equals(user.getStatus(), request.getStatus())) {
            shouldInvalidateToken = true;
        }
        user.setStatus(request.getStatus());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            shouldInvalidateToken = true;
        }
        if (request.getId() == null) {
            sysUserMapper.insert(user);
        } else {
            List<Long> oldRoleIds = sysUserMapper.selectRoleIdsByUserId(user.getId());
            if (!sameElements(oldRoleIds, request.getRoleIds())) {
                shouldInvalidateToken = true;
            }
            if (shouldInvalidateToken) {
                bumpTokenVersion(user);
            }
            sysUserMapper.updateById(user);
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        }
        saveUserRoles(user.getId(), request.getRoleIds());
        return new SysUserVO(user.getId(), user.getUsername(), user.getNickname(), user.getStatus(),
                sysUserMapper.selectRoleIdsByUserId(user.getId()), sysUserMapper.selectRoleNamesByUserId(user.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        sysUserMapper.deleteById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserPassword(Long id, ResetPasswordRequest request) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setTokenVersion((user.getTokenVersion() == null ? 0 : user.getTokenVersion()) + 1);
        sysUserMapper.updateById(user);
    }

    @Override
    public PageResult<SysRoleVO> listRoles(long current, long size, String keyword, Integer status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), SysRole::getRoleCode, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), SysRole::getRoleName, keyword)
                .eq(status != null, SysRole::getStatus, status)
                .orderByDesc(SysRole::getCreateTime);
        Page<SysRole> page = sysRoleMapper.selectPage(new Page<>(current, size), wrapper);
        List<SysRoleVO> records = page.getRecords().stream().map(role -> new SysRoleVO(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getStatus(),
                sysRoleMapper.selectPermissionIdsByRoleId(role.getId()),
                sysRoleMapper.selectPermissionNamesByRoleId(role.getId())
        )).toList();
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRoleVO saveRole(SysRoleSaveRequest request) {
        SysRole role;
        List<Long> affectedUserIds = List.of();
        if (request.getId() == null) {
            role = new SysRole();
        } else {
            role = sysRoleMapper.selectById(request.getId());
            if (role == null) {
                throw new BusinessException("角色不存在");
            }
            affectedUserIds = sysUserMapper.selectUserIdsByRoleId(role.getId());
        }
        boolean shouldInvalidateToken = request.getId() != null
                && (!Objects.equals(role.getStatus(), request.getStatus())
                || !sameElements(sysRoleMapper.selectPermissionIdsByRoleId(role.getId()), request.getPermissionIds()));
        BeanUtils.copyProperties(request, role);
        if (request.getId() == null) {
            sysRoleMapper.insert(role);
        } else {
            sysRoleMapper.updateById(role);
            sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId()));
        }
        saveRolePermissions(role.getId(), request.getPermissionIds());
        if (shouldInvalidateToken) {
            bumpTokenVersionBatch(affectedUserIds);
        }
        return new SysRoleVO(role.getId(), role.getRoleCode(), role.getRoleName(), role.getStatus(),
                sysRoleMapper.selectPermissionIdsByRoleId(role.getId()), sysRoleMapper.selectPermissionNamesByRoleId(role.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        if (sysRoleMapper.selectById(id) == null) {
            throw new BusinessException("角色不存在");
        }
        List<Long> affectedUserIds = sysUserMapper.selectUserIdsByRoleId(id);
        sysRoleMapper.deleteById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
        bumpTokenVersionBatch(affectedUserIds);
    }

    @Override
    public PageResult<SysPermission> listPermissions(long current, long size, String keyword, Integer status) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), SysPermission::getPermissionCode, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), SysPermission::getPermissionName, keyword)
                .eq(status != null, SysPermission::getStatus, status)
                .orderByAsc(SysPermission::getModuleName)
                .orderByAsc(SysPermission::getPermissionCode);
        Page<SysPermission> page = sysPermissionMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysPermission savePermission(SysPermissionSaveRequest request) {
        SysPermission permission;
        List<Long> affectedUserIds = List.of();
        if (request.getId() == null) {
            permission = new SysPermission();
        } else {
            permission = sysPermissionMapper.selectById(request.getId());
            if (permission == null) {
                throw new BusinessException("权限不存在");
            }
            affectedUserIds = sysUserMapper.selectUserIdsByPermissionId(permission.getId());
        }
        boolean shouldInvalidateToken = request.getId() != null
                && !Objects.equals(permission.getStatus(), request.getStatus());
        BeanUtils.copyProperties(request, permission);
        if (request.getId() == null) {
            sysPermissionMapper.insert(permission);
        } else {
            sysPermissionMapper.updateById(permission);
        }
        if (shouldInvalidateToken) {
            bumpTokenVersionBatch(affectedUserIds);
        }
        return sysPermissionMapper.selectById(permission.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        if (sysPermissionMapper.selectById(id) == null) {
            throw new BusinessException("权限不存在");
        }
        List<Long> affectedUserIds = sysUserMapper.selectUserIdsByPermissionId(id);
        sysPermissionMapper.deleteById(id);
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getPermissionId, id));
        bumpTokenVersionBatch(affectedUserIds);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        roleIds.forEach(roleId -> {
            SysUserRole relation = new SysUserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            sysUserRoleMapper.insert(relation);
        });
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        permissionIds.forEach(permissionId -> {
            SysRolePermission relation = new SysRolePermission();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            sysRolePermissionMapper.insert(relation);
        });
    }

    private boolean sameElements(List<Long> source, List<Long> target) {
        if (source == null || target == null) {
            return Objects.equals(source, target);
        }
        return source.size() == target.size() && source.containsAll(target) && target.containsAll(source);
    }

    private void bumpTokenVersion(SysUser user) {
        user.setTokenVersion((user.getTokenVersion() == null ? 0 : user.getTokenVersion()) + 1);
    }

    private void bumpTokenVersionBatch(List<Long> userIds) {
        userIds.stream().distinct().forEach(userId -> {
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                bumpTokenVersion(user);
                sysUserMapper.updateById(user);
            }
        });
    }
}
