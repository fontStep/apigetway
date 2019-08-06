package com.jst.apigetway.common.core;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ApiRunnable {

    /**
     *
     */
    String apiName;
    /**
     * ioc bean 名称
     */
    String baenName;
    /**
     * 实例
     */
    Object beanInstance;
    /**
     * 目标方法 getUser
     */
    Method targetMethod;
}
