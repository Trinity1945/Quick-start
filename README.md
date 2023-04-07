# 快速开发模板

## 框架与技术栈

- Spring Boot 2.7.10
- Spring MVC
- Spring AOP
- Mybatis+Mybatis Plus
- RSA加密算法
- MySql数据库

## 业务特性

1. 统一返回、自定义错误码、全局异常处理、通用响应类等
2. 全局跨域处理
3. 项目密码加密使用RSA非对称加密，公钥私钥自动保存到本地文件
4. 用户登录、注册、注销、删除、更新、检索等
5. AOP权限校验、基于AOP的异步事件日志记录等
6. 项目已经编写了实体到表结构的工具类，只需修改数据库连接即可一键自动生成表结构，无需再手动创建数据库表
7. 项目接口文档使用Kenife4j,浏览地址：http://localhost:9000/doc.html
## 项目结构
```java
  src
    ├─main
    │  ├─java
    │  │  └─com
    │  │      └─zhangyh
    │  │          └─management
    │  │              ├─admin
    │  │              │  ├─annotation 
    │  │              │  ├─aspect
    │  │              │  ├─config
    │  │              │  ├─controller
    │  │              │  ├─mapper
    │  │              │  ├─model
    │  │              │  │  ├─dto   //数据传入实体
    │  │              │  │  ├─po  	//数据库对应实体
    │  │              │  │  └─vo 	//返回前端实体
    │  │              │  └─service
    │  │              │      └─impl
    │  │              └─common
    │  │                  ├─config   //全局配置
    │  │                  ├─constants
    │  │                  ├─exception //异常及处理
    │  │                  ├─http
    │  │                  │  └─response //统一返回
    │  │                  ├─listener //异步事件监听
    │  │                  └─util
    │  │                      └─sychronize //同步实体到数据库工具
    │  │                          ├─annotation
    │  │                          ├─config
    │  │                          ├─core
    │  │                          │  └─scanner
    │  │                          ├─sql
    │  │                          │  └─type
    │  │                          └─table
    │  └─resources
    │      ├─mapper
    │      ├─static
    │      └─templates
    └─test
        └─java
            └─com
                └─zhangyh
                    └─management

```