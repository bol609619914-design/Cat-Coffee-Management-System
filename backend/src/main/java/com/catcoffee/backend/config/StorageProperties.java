package com.catcoffee.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String type = "local";
    private String uploadDir = "./uploads";
    private String publicBaseUrl = "http://localhost:8080";
}
