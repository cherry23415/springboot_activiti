package com.ying;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lyz
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableAsync
@EnableTransactionManagement //启用事务
@EnableAspectJAutoProxy
public class SpringbootActivitiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootActivitiApplication.class, args);
    }
}
