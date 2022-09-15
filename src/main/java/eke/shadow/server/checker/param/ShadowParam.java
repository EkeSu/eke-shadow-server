package eke.shadow.server.checker.param;

import eke.shadow.server.common.OperationType;
import lombok.Data;
import org.apache.ibatis.mapping.SqlCommandType;

import java.io.Serializable;
import java.util.Set;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/8
 */
@Data
public class ShadowParam implements Serializable {
    /**
     * mapperId eg: com.eke.know.core.mapper.KnowMapper.update
     */
    private String mapperPointId;

    /**
     * reference table name
     */
    private Set<String> tableNames;

    private SqlCommandType sqlCommandType;

    /**
     * sql parameter
     */
    private Object parameterObject;

    public OperationType getOperationType(){
        if(sqlCommandType.equals(SqlCommandType.SELECT)){
            return OperationType.READ;
        }else if(sqlCommandType.equals(SqlCommandType.INSERT)
                || sqlCommandType.equals(SqlCommandType.UPDATE)
                || sqlCommandType.equals(SqlCommandType.DELETE)
        ){
            return OperationType.WRITE;
        }

        return OperationType.UNKNOW;
    }

}
