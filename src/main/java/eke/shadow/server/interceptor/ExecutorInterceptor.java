package eke.shadow.server.interceptor;

import cn.hutool.core.util.StrUtil;
import eke.shadow.server.checker.ShadowChecker;
import eke.shadow.server.checker.param.ShadowParam;
import eke.shadow.server.common.OperationType;
import eke.shadow.server.common.SupportType;
import eke.shadow.server.config.DataSourceKey;
import eke.shadow.server.config.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/6
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
@Component
@Slf4j
public class ExecutorInterceptor implements Interceptor {

    @Autowired
    private List<ShadowChecker> shadowCheckerList;

    private static Pattern insertPattern = Pattern.compile("(?<=into )(.*?)(?=\\()");
    private static Pattern updatePattern = Pattern.compile("(?<=update)(.*?\\S)(?=\\ )");
    private static List<Pattern> selectPattern = new ArrayList<>();

    // 准备好正则表达式匹配器
    {
        selectPattern.add(Pattern.compile("(?<=join)(.*?\\S)(?=\\ )"));
        selectPattern.add(Pattern.compile("(?<=from)(.*?\\S)(?=\\ )"));
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("================ ExecutorInterceptor intercept... =====================");
        if(invocation.getTarget() instanceof Executor){
            // 解析 invocation 内容，获得 shadow 检查器 需要的参数
            ShadowParam checkParam = getCheckParam(invocation);

            // 获取 未检查通过的 检查器 Optional
            Optional<ShadowChecker> first = shadowCheckerList.stream().filter
                    (o ->
                            isSupport(o, checkParam.getOperationType()) && !o.isShadow(checkParam)
                    ).findFirst();

            // 有检查不通过的，到主库，日志记录
            if(first.isPresent()){
                log.info("================ ExecutorInterceptor intercept isShadow=false, cheker={}", first.get().getClass());
                DataSourceContextHolder.setDataSourceKey(DataSourceKey.MAIN);
            }else{
                DataSourceContextHolder.setDataSourceKey(DataSourceKey.SHADOW);
            }
        }

        return invocation.proceed();
    }

    private ShadowParam getCheckParam(Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Set<String> tableNames = parseTableName(ms, invocation.getArgs()[1]);

        ShadowParam checkParam = new ShadowParam();
        checkParam.setMapperPointId(ms.getId());
        checkParam.setSqlCommandType(ms.getSqlCommandType());
        checkParam.setTableNames(tableNames);
        checkParam.setParameterObject(invocation.getArgs()[1]);

        return checkParam;
    }

    private Set<String> parseTableName(MappedStatement arg, Object parameterObj) {
        BoundSql boundSql = arg.getBoundSql(parameterObj);
        String sql = boundSql.getSql();
        Set<String> tableList = new HashSet<>();
        if(SqlCommandType.INSERT.equals(arg.getSqlCommandType())){
            tableList.add(matchTableName(sql, insertPattern));
        }else if(SqlCommandType.UPDATE.equals(arg.getSqlCommandType())){
            tableList.add(matchTableName(sql, updatePattern));
        }else{
            selectPattern.forEach(o->{
                tableList.add(matchTableName(sql, o));
            });
        }

        return tableList;
    }

    private String matchTableName(String sql, Pattern o) {
        Matcher matcher = o.matcher(sql);
        if(matcher.find()){
            return matcher.group();
        }

        return StrUtil.EMPTY;
    }

    private boolean isSupport(ShadowChecker checker,OperationType operationType) {
        SupportType suppotrType = checker.getSuppotrType();
        if(suppotrType.equals(SupportType.ALL) || suppotrType.name().equals(operationType.name())){
            return true;
        }

        return false;
    }

    public static void main1(String[] args) {
        // 查找的字符串
        String str = "select t.id,t.name INTO from imas_xmhk_ll t left join urrp_db.imas_ckbx_bl a on t.id=a.id";

        //Java正则表达式以括号分组，第一个括号表示以"IMAS_"开头，第三个括号表示以" "(空格)结尾，中间括号为目标值，
//        Pattern p = Pattern.compile("(INTO )(.*?)(\\()");
        Pattern compile = Pattern.compile("(?<=from\\ ?)(.*?)(?=\\()");
        Matcher m = compile.matcher(str.toUpperCase());

        while(m.find()){
            // 取出目标值
            String matchStr = m.group(2);
            System.out.println("匹配内容："+matchStr);
            str = str.toUpperCase().replace(matchStr,matchStr+"_HL");
            System.out.println(str);
        }
    }


    public static void main(String[] args) {
        String sql="select * from    know  t  left join message ms on ( id,\n" +
                "title,\n" +
                "content )  VALUES  ( ?,\n" +
                "?,\n" +
                "? )";
//        String reg = "(?<=INTO).(?=\\()";
        String reg = "INTO";
        Matcher matcher = Pattern.compile(reg).matcher(sql);
//        String group = matcher.group();
//        System.out.println(group);
//        INTO(.*?)\(
        String filetext = "//@张小名: 25分//@李小花: 43分//@王力: 100分";
        Pattern p = Pattern.compile("(?<=join)(.*?\\S)(?=\\ )");//正则表达式，取=和|之间的字符串，不包括=和|
        Matcher m = p.matcher(sql.toLowerCase());

        while(m.find()) {
            System.out.println(m.group(0).trim());//m.group(1)不包括这两个字符
        }


    }


    private static List<String> getReferenceTableNames(String boundSql){
        List<String> result = new ArrayList<>(5);
        String[] s = boundSql.split(" ");
        Arrays.stream(s).forEach(System.out::println);

        return result;
    }


    @Override
    public Object plugin(Object target) {
        log.info("================ ExecutorInterceptor plugin... =====================");
        log.info("================ ExecutorInterceptor target={}}", target);
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
