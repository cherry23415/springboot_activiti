package com.ying.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author lyz
 */
@Configuration
public class Config {
    @Value("${domain}")
    public String domain;
}
