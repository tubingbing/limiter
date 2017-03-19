package tbb.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import tbb.LimiterTest;
import tbb.annotation.Limiter;
import tbb.constant.LimiterEnum;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


/**
 * User: tubingbing
 * Date: 2017/3/18
 * Time: 9:21
 */
@Aspect
@Component
public class LimiterAspectj {
    private static long time = System.currentTimeMillis();
    //是否第一次请求，是则重置当前时间
    private static final AtomicBoolean firstRequest = new AtomicBoolean(true);
    private static final AtomicLong fddd = new AtomicLong(0);
    private static final AtomicLong fddd2 = new AtomicLong(0);

    @Pointcut("@annotation(tbb.annotation.Limiter)")
    public void poincut(){

    }

    @Around("poincut()")
    public Object round(ProceedingJoinPoint pjp) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Limiter limiter= method.getAnnotation(Limiter.class);
        String value = limiter.value();
        long qps = limiter.qps();
        LimiterEnum typeEnum = limiter.type();
        if (value!=null && !value.equals("")) {
            if (!LimiterTest.limiter(value, qps)) {
                System.out.println("bu通过");
                return null;
            }
        }
        /*if(firstRequest.getAndSet(false)){
            time = end;
        }*/
        /*System.out.println(end-time+"---"+end);
        if((end-time)/1000%2!=0){ //超过限流大小，不执行后面的程序
            return null;
        }*/
        long end = System.currentTimeMillis();
        /*if(firstRequest.getAndSet(false)){
            time = end;
        }*/

        /*long s = fddd.incrementAndGet();
        System.out.print(end-time+"---");
        if(end-time>1000){
            time=end;
            fddd.set(0);
        }
        if(s>qps){
            //time = end;
            if(s>2*qps) {
                fddd.set(1);
            } else{
                System.out.println();
                return null;
            }
        }*/
        Object result = pjp.proceed();
        return result;
    }

}
