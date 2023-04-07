package com.zhangyh.management.common.util.sychronize.core.scanner;

import com.zhangyh.management.common.util.sychronize.annotation.EnableSync;
import com.zhangyh.management.common.util.sychronize.annotation.SqlSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyh
 * @Date 2023/3/24 9:30
 * @desc 注解扫描
 */
@Slf4j
public class CustomScannerRegistrar implements ResourceLoaderAware, ImportBeanDefinitionRegistrar , BeanClassLoaderAware  {

    ResourceLoader resourceLoader;
    ClassLoader classLoader;

    //保存实例和注解信息，用于构建tableSchema
    private static final Map<Class<?>,Map<String,Object>> INSTANT_INFO=new ConcurrentHashMap<>();

    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableSync.class.getName()));
        String[] sqlSynchronizeBasePackage = new String[0];
        if (attributes != null) {
            // 获取需要扫描的包
            sqlSynchronizeBasePackage = attributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        //没有配置则扫描注解所在的包
        if (sqlSynchronizeBasePackage.length == 0) {
            //获取注解所在类的包名
            sqlSynchronizeBasePackage = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        CustomScanner sqlSync = new CustomScanner(registry, SqlSync.class);
        if(resourceLoader!=null){
            sqlSync.setResourceLoader(resourceLoader);
        }
        int sqlSyncNum = sqlSync.scan(sqlSynchronizeBasePackage);
        log.info("SqlSync注解描的数量 [{}]",sqlSyncNum);
        for(String basePackage:sqlSynchronizeBasePackage){
            //获取basePackage下的所有候选类
            Set<BeanDefinition> sqlSyncCandidateComponents = sqlSync.findCandidateComponents(basePackage);
            //将符合条件的注入容器
            for (BeanDefinition candidateComponent : sqlSyncCandidateComponents) {
                if(candidateComponent instanceof AnnotatedBeanDefinition){
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMeta = beanDefinition.getMetadata();
//                    System.out.println(beanDefinition.getBeanClassName());
                    //不能是一个接口
                    Assert.isTrue(!annotationMeta.isInterface(), "@SqlSync cannot be placed on an interface");
                    //将标记了注解的类加载进来
                    Class<?> bean;
                    String beanClassName = beanDefinition.getBeanClassName();
                    try {
                         bean = ClassUtils.forName(Objects.requireNonNull(beanClassName), classLoader);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    // 获取@SqlSync注解的属性
                    AnnotationAttributes attr = AnnotationAttributes.fromMap(annotationMeta.getAnnotationAttributes(SqlSync.class.getCanonicalName()));
                    assert attr != null;
                    INSTANT_INFO.put(bean,attr);
                }
            }
        }
    }

    public static Map<Class<?>,Map<String,Object>> getInstantInfo(){
        return INSTANT_INFO;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }
}
