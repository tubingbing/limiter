package com.tbb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import com.tbb.annotation.Limiter;
import com.tbb.constant.LimiterEnum;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: tubingbing
 * Date: 2017/3/18
 * Time: 10:10
 */
@Component
public class Test {

    @Limiter(value="a",qps=3,type = LimiterEnum.REDIS_LUA)
    public void mss(){
        System.out.println("--------------------通过");
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");
        final Test t = (Test) context.getBean("test");

        ExecutorService service = Executors.newFixedThreadPool(4);
       final Random r = new Random();
        for(int i=0;i<20;i++){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            service.execute(new Runnable() {
                public void run() {
                    t.mss();
                }
            });
        }
        service.shutdown();
     }
}
