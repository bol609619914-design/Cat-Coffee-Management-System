package com.catcoffee.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    private String allowedOrigin;
}
