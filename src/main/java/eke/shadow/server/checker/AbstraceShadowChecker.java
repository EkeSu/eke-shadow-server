package eke.shadow.server.checker;

import eke.shadow.server.config.ShadowProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/9
 */
public abstract class AbstraceShadowChecker implements ShadowChecker{

    @Autowired
    ShadowProperties shadowProperties;

    @Autowired
    Environment environment;


    public void setEnvironment(Environment environment){
        this.environment = environment;
    }


}
