package eke.shadow.server;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import eke.shadow.server.config.DataSourceKey;
import eke.shadow.server.config.ShadowRoutingDataSource;
import eke.shadow.server.config.DataSourceContextHolder;
import eke.shadow.server.config.ShadowProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description：
 * @Author：eke
 * @Date：2022/9/5
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "shadow.enable", havingValue = "true")
@ComponentScan("eke.shadow.server")
public class ShdowDBConfigufation  {

    @Autowired
    private ShadowProperties shadowDBProperties;


    @Autowired
    private MybatisPlusAutoConfiguration mybatisPlusAutoConfiguration;


    @Bean("shadow")
    @ConfigurationProperties(prefix = "shadow.datasource")
    public DataSource dataSourceShadow() {
        return new DruidDataSource();
    }

    @Bean("main")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSourceMain() {
        return new DruidDataSource();
    }


    public ShdowDBConfigufation(){
        log.info("=========  ShdowDBConfigufation...  ============");
//        initShadowDB();
    }


    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource(@Qualifier("shadow") DataSource shadowDataSource,
                                        @Qualifier("main") DataSource mainDataSource) {

        ShadowRoutingDataSource dynamicRoutingDataSource = new ShadowRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>(3);
        dataSourceMap.put(DataSourceKey.MAIN.name(), mainDataSource);
        dataSourceMap.put(DataSourceKey.SHADOW.name(), shadowDataSource);

        dynamicRoutingDataSource.setDefaultTargetDataSource(mainDataSource);
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        DataSourceContextHolder.getDataSourceKeys().addAll(dataSourceMap.keySet());

        return dynamicRoutingDataSource;
    }


    @Bean
    @Primary
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource) throws Exception {
        return mybatisPlusAutoConfiguration.sqlSessionFactory(dataSource);
    }

}
