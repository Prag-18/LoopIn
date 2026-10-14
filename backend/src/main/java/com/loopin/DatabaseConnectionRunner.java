package com.loopin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Diagnostic component to verify and log MySQL JDBC Connectivity on application startup.
 */
@Component
public class DatabaseConnectionRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionRunner.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionRunner(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("==========================================================");
        logger.info("Verifying MySQL JDBC Connectivity...");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info(">>> SUCCESS: MySQL JDBC Connection Established! <<<");
            logger.info("Database Product : {} (Version: {})", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            logger.info("JDBC Driver      : {} (Version: {})", metaData.getDriverName(), metaData.getDriverVersion());
            logger.info("Database URL     : {}", metaData.getURL());
            logger.info("Connected User   : {}", metaData.getUserName());

            Integer requestCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_requests", Integer.class);
            Integer listingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_listings", Integer.class);

            logger.info("Database Status  : Table 'item_requests' has {} rows, 'item_listings' has {} rows", 
                    requestCount, listingCount);
        } catch (Exception ex) {
            logger.error("Failed to connect to MySQL database via JDBC!", ex);
            throw ex;
        }

        logger.info("==========================================================");
    }
}
