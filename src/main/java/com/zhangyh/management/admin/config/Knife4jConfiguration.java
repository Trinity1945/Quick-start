package com.zhangyh.management.admin.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zhangyh
 * @Date 2023/4/6 17:40
 * @desc
 */
@Configuration
// 必须启用swagger
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfiguration {
    @Bean
    public Docket defaultApi2() {
        return  new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                // 这里指定Controller扫描包路径(项目路径也行)
                // 方式一：配置扫描：所有想要在swagger界面统一管理的接口，都必须在此包下
                 .apis(RequestHandlerSelectors.basePackage("com.zhangyh.management"))
                // 方式二：只有当方法上有@ApiOperation注解时，才能生成对应的接口文档
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 路径使用any风格（指定所有路径）
                .paths(PathSelectors.any())
                .build();

    }

    /*
     * 设置文档信息主页的内容说明
     * @param:
     * @return: springfox.documentation.service.ApiInfo 文档信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("接口项目 后台服务API接口文档")
                .description("服务相关接口(knife4j)")
                // 服务Url（网站地址）
                .termsOfServiceUrl("http://localhost:9000/")
                .contact(new Contact("liziyuan",null,"mc1753343931"))
                .version("1.0")
                .build();
    }

}
