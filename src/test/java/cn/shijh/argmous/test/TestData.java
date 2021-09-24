package cn.shijh.argmous.test;


import cn.shijh.argmous.annotation.bean.Range;
import lombok.Data;

import java.util.List;

@Data
public class TestData {
    private String string;

    @Range({"1000",""})
    private Integer integer;

    private Double aDouble;

    private List<String> list;
}
