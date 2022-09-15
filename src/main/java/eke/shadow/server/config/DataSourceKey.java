package eke.shadow.server.config;

import lombok.Getter;

/**
 * @author eke
 */

@Getter
public enum DataSourceKey {
    /** shadow DB */
    SHADOW,

    /** 主库 */
    MAIN,

    ;

}