package com.zhangyh.management;

import com.zhangyh.management.common.util.sychronize.annotation.EnableSync;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAsync
@EnableSync(basePackage = "com.zhangyh.management.admin.model.po")
@SpringBootApplication
public class ManagementCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementCenterApplication.class, args);
    }

}
