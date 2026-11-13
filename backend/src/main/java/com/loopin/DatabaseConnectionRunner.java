package com.loopin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Diagnostic component to verify and log JDBC Connectivity on application startup.
 * Connects to the database through the HikariCP DataSource and executes
 * verification queries using Spring's JdbcTemplate.
 */
@Component
@Order(1)
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
        logger.info("Verifying JDBC Database Connectivity (HikariCP / JdbcTemplate)...");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info(">>> SUCCESS: JDBC Database Connection Established! <<<");
            logger.info("Database Product : {} (Version: {})", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            logger.info("JDBC Driver      : {} (Version: {})", metaData.getDriverName(), metaData.getDriverVersion());
            logger.info("Database URL     : {}", metaData.getURL());
            logger.info("Connected User   : {}", metaData.getUserName());

            // Query via JdbcTemplate to verify SQL execution over JDBC
            try {
                Integer requestCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_requests", Integer.class);
                logger.info("JDBC Query Check : Table 'item_requests' verified via JdbcTemplate (count: {})", requestCount);
            } catch (Exception e) {
                logger.info("JDBC Query Check : Schema ready for auto-provisioning.");
            }
        } catch (Exception ex) {
            logger.error("Failed to establish JDBC Connection!", ex);
            throw ex;
        }

        logger.info("==========================================================");
    }
}

