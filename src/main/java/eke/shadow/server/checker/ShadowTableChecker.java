package eke.shadow.server.checker;

import cn.hutool.core.util.StrUtil;
import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.SupportType;

import java.util.List;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/10
 */
public class ShadowTableChecker extends AbstraceShadowChecker{
    @Override
    public SupportType getSuppotrType() {
        return SupportType.ALL;
    }

    @Override
    public Boolean isShadow(ShadowParam shadowParam) {
        String shadowTables = environment.getProperty("shadow.confi.shadowTables");
        List<String> shadowTableList = StrUtil.splitTrim(shadowTables, StrUtil.COMMA);
        for (String table : shadowTableList) {
            if(shadowParam.getTableNames().contains(table)){
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }
}
