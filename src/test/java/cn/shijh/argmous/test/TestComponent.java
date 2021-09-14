package cn.shijh.argmous.test;

import cn.shijh.argmous.annotation.ArrayParamCheck;
import cn.shijh.argmous.annotation.ParamCheck;
import cn.shijh.argmous.annotation.ParamChecks;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestComponent {

    @ParamChecks(id = "test1", value = {
            @ParamCheck(include = "string", size = {2,-1}, regexp = "a.*"),
            @ParamCheck(include = {"integer", "aDouble"}, range = {"1", "10"}),
            @ParamCheck(include = "list", size = {-1, 4})
    })
    public void test(TestData data) {
        System.out.println("pass");
    }

    @ParamChecks(id = "test2", value = {
            @ParamCheck(include = "string", size = 3, regexp = "a.*"),
            @ParamCheck(include = "aDouble", range = {"1.2", "3.13"})
    })
    public void test2(TestData data) {
        System.out.println("pass");
    }

    @ArrayParamCheck(id="test3", value = {
            @ParamCheck(include = "string", size = {2,-1}, regexp = "a.*"),
            @ParamCheck(include = {"integer", "aDouble"}, range = {"1", "10"})
    })
    public void arrayTest(List<TestData> dataList) {
        System.out.println("pass");
    }

    @ParamCheck(custom = "customKey=true")
    public void testCustom(String s) {
        System.out.println("pass");
    }
}
