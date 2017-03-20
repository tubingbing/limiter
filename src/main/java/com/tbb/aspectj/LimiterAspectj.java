package com.tbb.aspectj;

import com.tbb.algorithm.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.stereotype.Component;
import com.tbb.annotation.Limiter;
import org.w3c.dom.Element;

import java.lang.reflect.Method;

/**
 * User: tubingbing
 * Date: 2017/3/18
 * Time: 9:21
 */
@Aspect
@Component
public class LimiterAspectj {

    @Pointcut("@annotation(com.tbb.annotation.Limiter)")
    public void poincut() {
    }

    @Around("poincut()")
    public Object round(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Limiter limiter = method.getAnnotation(Limiter.class);
        String value = limiter.value();
        boolean flag;
        long qps = limiter.qps();
        switch (limiter.type()) {
            case SIMPLE_COUNT:  //简单计数
                flag = SimpleCount.limiter(value, qps);
                break;
            case SMOOTH_COUNT:  //平滑计数
                flag = SmoothCount.limiter(value, qps);
                break;
            case LEAKY_BUCKET: //漏桶
                flag = LeakyBucket.limiter(value, qps);
                break;
            case TOKEN_BUCKET: //令牌桶 有token执行，未获取到返回false
                flag = TokenBucket.limiter(value, qps);
                break;
            case REDIS_LUA: //
                flag = RedisLua.limiter(value, qps);
                break;
            default: //默认令牌桶 其他请求等待获取token,不拒绝请求
                flag = TokenBucket.waitRequest(value, qps);
                break;
        }
        if (!flag) {
            System.out.println("============================不通过");//测试打印
            return null;
        }
        Object result = pjp.proceed();
        return result;
    }

}
