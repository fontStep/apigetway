package com.jst.apigetway.common.core;


import com.jst.apigetway.common.annotation.ApiController;
import com.jst.apigetway.common.annotation.ApiRequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Slf4j
public class ApiStore {

    private ApplicationContext applicationContext;

    /**
     * API 接口存储map
     */
    private Map<String, ApiRunnable> apiMap = new ConcurrentHashMap<>(16);

    public ApiStore(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     *
     */
    public void loadApiFromSpringBeans() {
        String[] names =  applicationContext.getBeanDefinitionNames();
        if(null != names && names.length>0){
            Class<?> clazz = null;
            for (String str: names
                 ) {
                /**
                 * 1.获取 class
                 */
                clazz = applicationContext.getType(str);
                if(clazz.isAnnotationPresent(ApiController.class)) {
                    /**
                     * 2.遍历 class下的所有方法
                     */
                    for (Method m : clazz.getDeclaredMethods()) {
                        // 通过反谢拿到APIMapping注解
                        ApiRequestMapping apiRequestMapping = m.getAnnotation(ApiRequestMapping.class);
                        if (apiRequestMapping != null) {
                            addApiItem(apiRequestMapping, clazz, m, str);
                        }
                    }
                }
            }
        }
    }

    private void addApiItem(ApiRequestMapping apiRequestMapping, Class<?> clazz, Method m,String beanName) {
        ApiRunnable apiRunnable = new ApiRunnable();
        apiRunnable.setApiName(apiRequestMapping.value());
        apiRunnable.setBaenName(beanName);
        apiRunnable.setTargetMethod(m);
        apiRunnable.setBeanInstance(applicationContext.getBean(beanName));
        apiMap.put(apiRequestMapping.value(),apiRunnable);
    }

    public ApiRunnable findApiRunnable(String apiName) {

        return apiMap.get(apiName);
    }

}
