package com.tbb.aspectj;

import com.tbb.annotation.Limiter;
import com.tbb.proxy.LimiterImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * User: tubingbing
 * Date: 2017/3/18
 * Time: 9:21
 */
@Aspect
public class LimiterAspectj  {

    private static final Logger logger = LoggerFactory.getLogger(LimiterAspectj.class);

    private String index;
    private String systemId;

    /**
     * 初始化
     */
    public void init(){
        LimiterImpl.init(this.index,this.systemId);
    }

    @Pointcut("@annotation(com.jd.o2o.annotation.Limiter)")
    public void poincut() {
    }

    @Around("poincut()")
    public Object round(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Limiter limiter = method.getAnnotation(Limiter.class);
        String value = limiter.value();
        boolean flag = LimiterImpl.limiter(value, limiter.type(), pjp.getArgs());
        if (flag) {
            return new Throwable("请求过于频繁，请稍后再试");
        }
        Object result = pjp.proceed();
        return result;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
