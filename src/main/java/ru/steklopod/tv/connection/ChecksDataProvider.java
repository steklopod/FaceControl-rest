package ru.steklopod.tv.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@ComponentScan(basePackages = "ru.steklopod.tv")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class ChecksDataProvider {

    @Value("${checks.datasource.username}")
    private String username;
    @Value("${checks.datasource.password}")
    private String password;
    @Value("${checks.datasource.url}")
    private String url;
    @Value("${checks.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${pool.size}")
    private int poolsize;
    @Value("${pool.connection.timeout}")
    private int connectionTimeOut;
    @Value("${pool.idle.timeout}")
    private int idleTimeOut;
    @Value("${pool.max.lifetime}")
    private int lifetime;


    @Bean(name = "TrCheck")
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(poolsize);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaxLifetime(lifetime);
        hikariConfig.setConnectionTimeout(connectionTimeOut);
        hikariConfig.setIdleTimeout(idleTimeOut);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setLeakDetectionThreshold(15000);
        hikariConfig.setPoolName("Hikari-2");


        hikariConfig.setIsolateInternalQueries(true);
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", true);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        return ds;
    }

    @Bean(name = "TrCheckTransactionManager")
    public DataSourceTransactionManager tManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

}