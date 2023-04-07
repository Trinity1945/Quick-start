# 快速开发模板

1. 基于SpringBoot集成了MybatisPlus，已做统一返回、全局异常处理等
2. 项目密码加密使用RSA非对称加密，公钥私钥自动保存到文件
3. 项目已经完成用户管理的基础增删改查分页操作
4. 项目已经编写了实体到表结构的工具类，只需修改数据库连接即可一键自动生成表结构，无需手动创建表
5. 项目实现简单权限校验、日志记录等
6. 项目接口文档使用Kenife4j,浏览地址：http://localhost:9000/doc.html
## 项目结构
```java
─src
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