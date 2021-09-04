package cn.shijh.argmous.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cn.shijh.argmous.spring.argmous")
public class ArgmousProperties {
    private Boolean enable = true;
    private Integer order;
}