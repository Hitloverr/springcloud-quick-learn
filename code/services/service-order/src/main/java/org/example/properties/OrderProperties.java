package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "order") //自动刷新
@Data
public class OrderProperties {
    private String timeout;
}
