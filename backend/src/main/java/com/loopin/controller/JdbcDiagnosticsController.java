package com.loopin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST Endpoint for inspecting live JDBC connectivity, DataSource configuration,
 * and executing queries via Spring's JdbcTemplate.
 */
@RestController
@RequestMapping("/api/jdbc")
@CrossOrigin(origins = "*")
public class JdbcDiagnosticsController {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public JdbcDiagnosticsController(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Diagnostic endpoint:
     * GET /api/jdbc/status
     * Returns metadata about the JDBC Driver, connection pool, and test queries.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getJdbcStatus() {
        Map<String, Object> status = new LinkedHashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            status.put("status", "CONNECTED");
            status.put("databaseProduct", metaData.getDatabaseProductName());
            status.put("databaseVersion", metaData.getDatabaseProductVersion());
            status.put("jdbcDriver", metaData.getDriverName());
            status.put("driverVersion", metaData.getDriverVersion());
            status.put("databaseUrl", metaData.getURL());
            status.put("databaseUser", metaData.getUserName());
            status.put("connectionPool", dataSource.getClass().getSimpleName());

            // Query counts via JdbcTemplate to prove raw JDBC execution
            try {
                Integer reqCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_requests", Integer.class);
                Integer listingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_listings", Integer.class);
                status.put("itemRequestsCountViaJdbc", reqCount != null ? reqCount : 0);
                status.put("itemListingsCountViaJdbc", listingCount != null ? listingCount : 0);
            } catch (Exception queryEx) {
                status.put("queryStatus", "Tables initializing");
            }

            return ResponseEntity.ok(status);
        } catch (Exception ex) {
            status.put("status", "ERROR");
            status.put("errorMessage", ex.getMessage());
            return ResponseEntity.internalServerError().body(status);
        }
    }
}

