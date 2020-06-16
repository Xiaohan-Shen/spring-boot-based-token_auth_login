package token_based_auth.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@NoArgsConstructor
@Setter
@Getter
public class RedisProperties {
    private String host;
    private Integer port;
    private String password;
    private Integer database;
    private Long timeout;
    private Integer maxActive;
    private Integer maxIdle;
    private Integer minIdle;
    private Integer maxWait;
}
