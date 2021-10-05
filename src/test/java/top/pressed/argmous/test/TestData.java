package top.pressed.argmous.test;



import lombok.Data;
import top.pressed.argmous.annotation.bean.Range;

import java.util.List;

@Data
public class TestData {
    private String string;

    @Range({"1000",""})
    private Integer integer;

    @Range({"-1", "2"})
    private Integer int2;

    private Double aDouble;

    private List<String> list;
}
