package com.catcoffee.backend.service;

import com.catcoffee.backend.dto.LoginRequest;
import com.catcoffee.backend.dto.ChangePasswordRequest;
import com.catcoffee.backend.dto.RefreshTokenRequest;
import com.catcoffee.backend.dto.RegisterRequest;
import com.catcoffee.backend.vo.LoginVO;
import com.catcoffee.backend.vo.UserProfileVO;

public interface AuthService {

    LoginVO login(LoginRequest request);

    LoginVO register(RegisterRequest request);

    LoginVO refreshToken(RefreshTokenRequest request);

    void logout();

    void changePassword(ChangePasswordRequest request);

    UserProfileVO currentUser();
}
