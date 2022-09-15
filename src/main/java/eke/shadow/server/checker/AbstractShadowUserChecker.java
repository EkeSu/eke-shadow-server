package eke.shadow.server.checker;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.SupportType;

import java.util.Arrays;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/9
 */

public abstract class AbstractShadowUserChecker extends AbstraceShadowChecker{
    @Override
    public SupportType getSuppotrType() {
        return SupportType.ALL;
    }

    @Override
    public Boolean isShadow(ShadowParam shadowParam) {
        String currentLoginUser = getCurrentLoginUser();
        if(StringUtils.isBlank(currentLoginUser)){
            return Boolean.FALSE;
        }

        String shadowUserPrefix = environment.getProperty("shadow.config.shadowUserPrefix");
        if(currentLoginUser.startsWith(shadowUserPrefix)){
            return Boolean.TRUE;
        }

        String specialShadowUser = environment.getProperty("shadow.config.specialShadowUsers");
        if(StringUtils.isNotBlank(specialShadowUser)){
            String[] split = StrUtil.split(specialShadowUser, StrUtil.COMMA);
            return Arrays.stream(split).filter(o->currentLoginUser.equals(o)).findFirst().isPresent();
        }

        return Boolean.TRUE;
    }

    abstract String getCurrentLoginUser();
}
