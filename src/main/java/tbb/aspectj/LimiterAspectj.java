package tbb.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import tbb.algorithm.SimpleCount;
import tbb.algorithm.SmoothCount;
import tbb.algorithm.TokenBucket;
import tbb.annotation.Limiter;

import java.lang.reflect.Method;

/**
 * User: tubingbing
 * Date: 2017/3/18
 * Time: 9:21
 */
@Aspect
@Component
public class LimiterAspectj {

    @Pointcut("@annotation(tbb.annotation.Limiter)")
    public void poincut(){
    }

    @Around("poincut()")
    public Object round(ProceedingJoinPoint pjp) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Limiter limiter= method.getAnnotation(Limiter.class);
        String value = limiter.value();
        boolean flag = true;
        long qps = limiter.qps();
        switch (limiter.type()){
            case SIMPLE_COUNT:  //简单计数
                flag = SimpleCount.limiter(value,qps);
                break;
            case SMOOTH_COUNT:  //平滑计数
                flag = SmoothCount.limiter(value,qps);
                break;
            case LEAKY_BUCKET: //漏桶
                break;
            case TOKEN_BUCKET: //令牌桶
                flag = TokenBucket.limiter(value,qps);
                break;
            default: //默认令牌桶
                flag = TokenBucket.limiter(value,qps);
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
