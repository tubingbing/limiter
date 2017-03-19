package com.tbb.annotation;

import com.tbb.constant.LimiterEnum;

import java.lang.annotation.*;

/**
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
    long qps() default 1000;
    LimiterEnum type() default LimiterEnum.TOKEN_BUCKET;



}
