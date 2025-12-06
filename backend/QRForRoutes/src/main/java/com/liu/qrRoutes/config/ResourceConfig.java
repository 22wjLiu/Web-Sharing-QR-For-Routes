package com.liu.qrRoutes.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties("resource")
public class ResourceConfig {

  private String rootUrl;

  private String avatarDir;

  private String avatarDefault;

  private String qrDir;
}
