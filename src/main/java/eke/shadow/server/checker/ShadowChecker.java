package eke.shadow.server.checker;

import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.SupportType;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/7
 */
public interface ShadowChecker  {

    SupportType getSuppotrType();


    Boolean isShadow(ShadowParam shadowParam);

}
