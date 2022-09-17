package eke.shadow.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author eke
 */
@Slf4j
public class ShadowRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey = DataSourceContextHolder.getDataSourceKey();
        log.info("当前数据源 [{}]", dataSourceKey);
        // remove threadlocal value
        DataSourceContextHolder.clearDataSourceKey();
        return dataSourceKey;
    }
}