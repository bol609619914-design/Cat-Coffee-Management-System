package com.catcoffee.backend.service.impl;

import com.catcoffee.backend.dto.LoginRequest;
import com.catcoffee.backend.dto.ChangePasswordRequest;
import com.catcoffee.backend.dto.RefreshTokenRequest;
import com.catcoffee.backend.dto.RegisterRequest;
import com.catcoffee.backend.entity.SysRole;
import com.catcoffee.backend.entity.SysUser;
import com.catcoffee.backend.entity.SysUserRole;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.SysRoleMapper;
import com.catcoffee.backend.mapper.SysUserMapper;
import com.catcoffee.backend.mapper.SysUserRoleMapper;
import com.catcoffee.backend.security.AuthUser;
import com.catcoffee.backend.security.AuthUserFactory;
import com.catcoffee.backend.security.JwtTokenProvider;
import com.catcoffee.backend.security.SecurityUtils;
import com.catcoffee.backend.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catcoffee.backend.vo.LoginVO;
import com.catcoffee.backend.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_USER_ROLE_CODE = "user";
    private static final String DEFAULT_USER_ROLE_NAME = "普通用户";

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUserFactory authUserFactory;

    @Override
    public LoginVO login(LoginRequest request) {
        SysUser sysUser = sysUserMapper.selectByUsername(request.getUsername());
        if (sysUser == null || !passwordEncoder.matches(request.getPassword(), sysUser.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        return buildLoginResult(authUserFactory.fromSysUser(sysUser));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String nickname = request.getNickname().trim();
        String password = request.getPassword().trim();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(nickname) || !StringUtils.hasText(password)) {
            throw new BusinessException("注册信息不能为空");
        }
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new BusinessException("用户名已存在，请更换后再试");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        user.setMemberPoints(0);
        user.setTokenVersion(0);
        sysUserMapper.insert(user);

        SysRole defaultRole = ensureDefaultUserRole();
        SysUserRole relation = new SysUserRole();
        relation.setUserId(user.getId());
        relation.setRoleId(defaultRole.getId());
        sysUserRoleMapper.insert(relation);

        return buildLoginResult(authUserFactory.fromSysUser(user));
    }

    @Override
    public LoginVO refreshToken(RefreshTokenRequest request) {
        if (!jwtTokenProvider.isValid(request.getRefreshToken(), JwtTokenProvider.REFRESH_TOKEN)) {
            throw new BusinessException("refreshToken 无效或已过期");
        }
        Map<String, Object> tokenMeta = jwtTokenProvider.parseTokenMeta(request.getRefreshToken());
        AuthUser authUser = authUserFactory.fromUserId((Long) tokenMeta.get("uid"));
        Integer tokenVersion = (Integer) tokenMeta.get("tokenVersion");
        if (authUser.getTokenVersion() == null || !authUser.getTokenVersion().equals(tokenVersion)) {
            throw new BusinessException("refreshToken 已失效，请重新登录");
        }
        return buildLoginResult(authUser);
    }

    @Override
    public void logout() {
        AuthUser authUser = SecurityUtils.getCurrentUser();
        SysUser sysUser = sysUserMapper.selectById(authUser.getId());
        sysUser.setTokenVersion((sysUser.getTokenVersion() == null ? 0 : sysUser.getTokenVersion()) + 1);
        sysUserMapper.updateById(sysUser);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        AuthUser authUser = SecurityUtils.getCurrentUser();
        SysUser sysUser = sysUserMapper.selectById(authUser.getId());
        if (!passwordEncoder.matches(request.getOldPassword(), sysUser.getPassword())) {
            throw new BusinessException("当前密码不正确");
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new BusinessException("新密码不能与当前密码相同");
        }
        sysUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUser.setTokenVersion((sysUser.getTokenVersion() == null ? 0 : sysUser.getTokenVersion()) + 1);
        sysUserMapper.updateById(sysUser);
    }

    @Override
    public UserProfileVO currentUser() {
        return buildProfile(SecurityUtils.getCurrentUser());
    }

    private UserProfileVO buildProfile(AuthUser authUser) {
        return new UserProfileVO(
                authUser.getId(),
                authUser.getUsername(),
                authUser.getNickname(),
                sysUserMapper.selectById(authUser.getId()).getMemberPoints(),
                authUser.getRoles(),
                authUser.getPermissions()
        );
    }

    private LoginVO buildLoginResult(AuthUser authUser) {
        return new LoginVO(
                jwtTokenProvider.generateAccessToken(authUser),
                jwtTokenProvider.generateRefreshToken(authUser),
                buildProfile(authUser)
        );
    }

    private SysRole ensureDefaultUserRole() {
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, DEFAULT_USER_ROLE_CODE)
                .eq(SysRole::getDeleted, 0)
                .last("LIMIT 1"));
        if (role != null) {
            return role;
        }

        SysRole createdRole = new SysRole();
        createdRole.setRoleCode(DEFAULT_USER_ROLE_CODE);
        createdRole.setRoleName(DEFAULT_USER_ROLE_NAME);
        createdRole.setStatus(1);
        sysRoleMapper.insert(createdRole);
        return createdRole;
    }
}
