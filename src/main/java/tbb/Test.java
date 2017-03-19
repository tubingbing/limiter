package tbb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import tbb.annotation.Limiter;

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

    @Limiter(value="a",qps=20)
    public void mss(){
        System.out.println("通过");
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");
        final Test t = (Test) context.getBean("test");

        ExecutorService service = Executors.newFixedThreadPool(4);
       final Random r = new Random();
        service.execute(new Runnable() {
            public void run() {
                t.mss();
            }
        });
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i=0;i<10;i++){
            service.execute(new Runnable() {
                public void run() {
                   /* try {
                        Thread.sleep(r.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    t.mss();
                }
            });
        }
        service.shutdown();
     }
}
