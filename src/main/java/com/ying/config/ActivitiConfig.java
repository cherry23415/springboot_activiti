package com.ying.config;

import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author lyz
 */
@Configuration
@ComponentScan("org.activiti.rest.diagram")
public class ActivitiConfig extends AbstractProcessEngineAutoConfiguration {

    @Override
    protected SpringProcessEngineConfiguration baseSpringProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager, SpringAsyncExecutor springAsyncExecutor) throws IOException {
        SpringProcessEngineConfiguration configuration = super.baseSpringProcessEngineConfiguration(dataSource, platformTransactionManager, springAsyncExecutor);
        configuration.setEnableDatabaseEventLogging(true);
        configuration.setProcessDefinitionCacheLimit(10);
        configuration.setActivityFontName("宋体");
        configuration.setLabelFontName("宋体");
        return configuration;
    }
}
