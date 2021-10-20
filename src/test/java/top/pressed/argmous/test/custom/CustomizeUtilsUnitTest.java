package top.pressed.argmous.test.custom;


import top.pressed.argmous.exception.ParamCheckException;
import top.pressed.argmous.test.TestApplication;
import top.pressed.argmous.test.TestComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestApplication.class)
@SuppressWarnings("all")
public class CustomizeUtilsUnitTest {
    @Autowired
    private TestComponent testComponent;

    @Test
    void customUtils() throws Exception {
        testComponent.testCustom("true");
    }

    @Test
    void test2() throws Exception {
        try {
            testComponent.testCustom("false");
        } catch (ParamCheckException e) {
            System.out.println(e.getMessage());
            return;
        }
        throw new IllegalStateException("cn.shijh.argmous.test fail");
    }
}
