package eke.shadow.server.checker;

import cn.hutool.core.util.StrUtil;
import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.SupportType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description：特例表 检查器，对特定表不进行 影子库读写
 * @Author：eke
 * @Date：2022/9/7
 */
@Order(90)
@Component
public class ExcludeTableChecker extends AbstraceShadowChecker {

    @Override
    public SupportType getSuppotrType() {
        return SupportType.ALL;
    }

    @Override
    public Boolean isShadow(ShadowParam shadowParam) {
        String excludeTables = environment.getProperty("shadow.config.excludeTables");
        List<String> excludeTableList = StrUtil.splitTrim(excludeTables, StrUtil.COMMA);
        for (String table : excludeTableList) {
            if(shadowParam.getTableNames().contains(table)){
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }
}
