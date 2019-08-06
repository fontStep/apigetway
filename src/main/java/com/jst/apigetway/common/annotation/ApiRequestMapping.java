package com.jst.apigetway.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  api
 *  @author wangjiawei
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRequestMapping {
    /**
     * 服务请求地址
     */
     String value();

}
