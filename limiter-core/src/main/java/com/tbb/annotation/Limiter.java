package com.tbb.annotation;

import com.tbb.constant.LimiterEnum;

import java.lang.annotation.*;

/**
 * 限流注解类（接入使用该自定义注解）
 * User: tubingbing
 * Date: 2017/3/18
 * Time: 9:17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Limiter {

    String value();
    LimiterEnum type() default LimiterEnum.SIMPLE_COUNT;
    //String limiterReturn() default "";//"{\"code:-1\",\"msg\":\"请求过于频繁，请稍后再试\"}";

}
