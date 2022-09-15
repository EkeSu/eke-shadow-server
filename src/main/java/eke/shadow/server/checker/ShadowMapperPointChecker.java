package eke.shadow.server.checker;

import cn.hutool.core.util.StrUtil;
import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.SupportType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description：指定 mapper Id 进行影子库读写
 * @Author：eke
 * @Date：2022/9/7
 */
@Order(80)
@Component
public class ShadowMapperPointChecker extends AbstraceShadowChecker {

    @Override
    public SupportType getSuppotrType() {
        return SupportType.ALL;
    }

    @Override
    public Boolean isShadow(ShadowParam shadowParam) {
        String property = environment.getProperty("shadow.config.shadowMapperPoints");
        if(StrUtil.isBlank(property)){
            return Boolean.TRUE;
        }

        Set<String> shadowMapperPoints = Arrays.stream(StrUtil.split(property, ",")).collect(Collectors.toSet());
        return shadowMapperPoints.contains(shadowParam.getMapperPointId());
    }

}
