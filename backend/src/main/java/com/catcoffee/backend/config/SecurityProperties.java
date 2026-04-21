package com.catcoffee.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private String jwtSecret;
    private Long accessTokenExpireHours;
    private Long refreshTokenExpireDays;
}
