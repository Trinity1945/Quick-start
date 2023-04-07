package com.zhangyh.management.common.util.sychronize.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author zhangyh
 * @Date 2023/3/29 8:24
 * @desc
 */
@Configuration
public class JdbcTemplateConfig {

    @Bean
    DataSource dataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    JdbcTemplate jdbcTemplateOne(@Qualifier("dataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    //事务处理
    @Bean
    public PlatformTransactionManager txManager1(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
