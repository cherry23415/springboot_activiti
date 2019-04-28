package com.ying.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lyz
 */
@Configuration
@MapperScan("com.ying.dao")
public class MyBatisConfig {
}