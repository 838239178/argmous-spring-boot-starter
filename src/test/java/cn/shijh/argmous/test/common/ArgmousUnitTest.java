package cn.shijh.argmous.test.common;

import cn.shijh.argmous.exception.ParamCheckException;
import cn.shijh.argmous.test.TestApplication;
import cn.shijh.argmous.test.TestComponent;
import cn.shijh.argmous.test.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(classes = TestApplication.class)
@SuppressWarnings("all")
public class ArgmousUnitTest {
    @Autowired
    private TestComponent testComponent;

    @Test
    void test1() throws Exception {
        TestData data = new TestData();
        data.setADouble(1.3);
        data.setInteger(9);
        data.setList(Arrays.asList("1","2"));
        data.setString("asa");
        testComponent.test(data);
    }

    @Test
    void test2() throws Exception {
        try {
            TestData data = new TestData();
            data.setADouble(1.3);
            data.setInteger(1997);
            data.setList(Arrays.asList("1","2"));
            data.setString("asa");
            testComponent.test(data);
            throw new IllegalStateException("cn.shijh.argmous.test fail");
        } catch (ParamCheckException e) {
            System.out.println("pass:" + e.getMessage());
        }
    }

    @Test
    void test3() throws Exception {
        TestData data = new TestData();
        data.setADouble(1.3);
        data.setInteger(99);
        data.setList(Arrays.asList("1","2"));
        data.setString("asa");
        testComponent.arrayTest(Arrays.asList(data,data,data));
    }

    @Test
    void test4() throws Exception {
        try {
            TestData data = new TestData();
            data.setADouble(1.3);
            data.setInteger(50);
            data.setList(Arrays.asList("1","2"));
            data.setString("bsa");
            testComponent.test(data);
            throw new IllegalStateException("cn.shijh.argmous.test fail");
        } catch (ParamCheckException e) {
            System.out.println("pass:" + e.getMessage());
        }
    }
}
