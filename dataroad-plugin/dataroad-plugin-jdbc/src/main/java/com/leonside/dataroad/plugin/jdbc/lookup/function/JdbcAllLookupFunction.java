package com.leonside.dataroad.plugin.jdbc.lookup.function;

import com.leonside.dataroad.flink.processor.lookup.function.AbstractAllLookupFunction;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.lookup.config.JdbcLookupConfig;
import com.leonside.dataroad.plugin.jdbc.utils.DbUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * @author leon
 */
public class JdbcAllLookupFunction extends AbstractAllLookupFunction {

    public static Logger LOG = LoggerFactory.getLogger(JdbcAllLookupFunction.class);

    protected JdbcLookupConfig jdbcLookupConfig;
    protected String query;
    protected DatabaseDialect databaseDialect;

    public JdbcAllLookupFunction(JdbcLookupConfig jdbcLookupConfig, DatabaseDialect databaseDialect) {
        super(jdbcLookupConfig);
        this.jdbcLookupConfig = jdbcLookupConfig;
        this.query = (StringUtils.isNotEmpty(jdbcLookupConfig.getCustomSql())) ?
                jdbcLookupConfig.getCustomSql() :
                databaseDialect.getSelectFromStatement(jdbcLookupConfig.getSchema(), jdbcLookupConfig.getTable(), baseLookupConfig.getColumns(), new String[] {});
        this.databaseDialect = databaseDialect;
    }

    @Override
    protected void loadAllData() {
        Connection connection = null;

        try {
            connection = DbUtil.getConnection(jdbcLookupConfig.getJdbcUrl(),jdbcLookupConfig.getUsername(), jdbcLookupConfig.getPassword());

            cacheLoadedData(connection);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.error("", e);
                }
            }
        }
    }

    /**
     * fill data
     * @param connection
     * @throws SQLException
     */
    private void cacheLoadedData(Connection connection) throws SQLException {
        // load data from table
        Statement statement = connection.createStatement();
        statement.setFetchSize(jdbcLookupConfig.getFetchSize());
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            try {
                Row row = Row.withNames();
                Arrays.stream(baseLookupConfig.getColumns())
                        .forEach(column->{
                            try {
                                row.setField(column, resultSet.getObject(column));
                            } catch (SQLException e) {
                                LOG.error("", e);
                            }
                        });

                buildCache(row);
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
    }

    public static class JdbcAllLookupFunctionBuilder {
        private JdbcLookupConfig jdbcLookupConfig;
        private DatabaseDialect databaseDialect;

        public JdbcAllLookupFunctionBuilder jdbcLookupConfig(JdbcLookupConfig jdbcLookupConfig){
            this.jdbcLookupConfig = jdbcLookupConfig;
            return this;
        }
        public JdbcAllLookupFunctionBuilder databaseDialect(DatabaseDialect databaseDialect){
            this.databaseDialect = databaseDialect;
            return this;
        }
        public JdbcAllLookupFunction build(){
            return new JdbcAllLookupFunction(jdbcLookupConfig, databaseDialect);
        }
    }

}
