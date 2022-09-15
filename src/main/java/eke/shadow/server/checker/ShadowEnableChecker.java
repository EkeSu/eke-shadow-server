package eke.shadow.server.checker;

import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.SupportType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description：order=100 哦，兜底
 * @Author：eke
 * @Date：2022/9/7
 */
@Order(100)
@Component
public class ShadowEnableChecker extends AbstraceShadowChecker {

    @Override
    public SupportType getSuppotrType() {
        return SupportType.ALL;
    }

    @Override
    public Boolean isShadow(ShadowParam shadowParam) {
        return Boolean.TRUE.toString().equals(environment.getProperty("shadow.enable"));
    }

}
