package eke.shadow.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/5
 */
@Data
@Component
@ConfigurationProperties(prefix = "shadow.config")
public class ShadowProperties {
    private String shadowTables;
    private String excludeTables;
    private String shadowUserPrefix;
    private String specialShadowUser;
    private String shadowMapperPoints;
}
