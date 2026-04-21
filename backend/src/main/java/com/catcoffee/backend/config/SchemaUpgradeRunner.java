package com.catcoffee.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaUpgradeRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        ensureColumn("reservation", "user_id",
                "ALTER TABLE reservation ADD COLUMN user_id BIGINT NULL AFTER id");
        ensureColumn("customer_order", "user_id",
                "ALTER TABLE customer_order ADD COLUMN user_id BIGINT NULL AFTER id");
        ensureIndex("reservation", "idx_reservation_user_id",
                "ALTER TABLE reservation ADD KEY idx_reservation_user_id (user_id)");
        ensureIndex("customer_order", "idx_order_user_id",
                "ALTER TABLE customer_order ADD KEY idx_order_user_id (user_id)");
    }

    private void ensureColumn(String tableName, String columnName, String ddl) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """, Integer.class, tableName, columnName);
        if (count != null && count == 0) {
            jdbcTemplate.execute(ddl);
            log.info("Schema upgraded: added column {}.{}", tableName, columnName);
        }
    }

    private void ensureIndex(String tableName, String indexName, String ddl) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND INDEX_NAME = ?
                """, Integer.class, tableName, indexName);
        if (count != null && count == 0) {
            jdbcTemplate.execute(ddl);
            log.info("Schema upgraded: added index {} on {}", indexName, tableName);
        }
    }
}
