package spring.framework;

import com.tianyou.spring.framework.context.TyApplicationContext;
import org.junit.Test;

/**
 * Created by tianyou on 2020/1/22.
 */
public class FrameworkTest {

    @Test
    public void TestSpringFramework(){
        TyApplicationContext context=new TyApplicationContext("classpath:application.properties");
        try {
            System.out.println(context.getBean("myAction"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
