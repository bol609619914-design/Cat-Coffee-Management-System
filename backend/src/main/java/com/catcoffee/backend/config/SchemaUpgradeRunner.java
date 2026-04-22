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
        ensureColumn("sys_user", "member_points",
                "ALTER TABLE sys_user ADD COLUMN member_points INT NOT NULL DEFAULT 0 AFTER nickname");
        ensureColumn("reservation", "user_id",
                "ALTER TABLE reservation ADD COLUMN user_id BIGINT NULL AFTER id");
        ensureColumn("customer_order", "user_id",
                "ALTER TABLE customer_order ADD COLUMN user_id BIGINT NULL AFTER id");
        ensureColumn("customer_order", "original_amount",
                "ALTER TABLE customer_order ADD COLUMN original_amount DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER table_id");
        ensureColumn("customer_order", "discount_amount",
                "ALTER TABLE customer_order ADD COLUMN discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER original_amount");
        ensureColumn("customer_order", "payable_amount",
                "ALTER TABLE customer_order ADD COLUMN payable_amount DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER total_amount");
        ensureColumn("customer_order", "points_used",
                "ALTER TABLE customer_order ADD COLUMN points_used INT NOT NULL DEFAULT 0 AFTER order_status");
        ensureColumn("customer_order", "points_awarded",
                "ALTER TABLE customer_order ADD COLUMN points_awarded INT NOT NULL DEFAULT 0 AFTER points_used");
        ensureColumn("customer_order", "user_coupon_id",
                "ALTER TABLE customer_order ADD COLUMN user_coupon_id BIGINT NULL AFTER points_awarded");
        ensureIndex("reservation", "idx_reservation_user_id",
                "ALTER TABLE reservation ADD KEY idx_reservation_user_id (user_id)");
        ensureIndex("customer_order", "idx_order_user_id",
                "ALTER TABLE customer_order ADD KEY idx_order_user_id (user_id)");

        ensureTable("member_point_flow", """
                CREATE TABLE member_point_flow (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    change_type VARCHAR(20) NOT NULL,
                    change_amount INT NOT NULL,
                    balance_after INT NOT NULL,
                    biz_type VARCHAR(30),
                    biz_id BIGINT,
                    remark VARCHAR(255),
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    KEY idx_point_user (user_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        ensureTable("coupon_template", """
                CREATE TABLE coupon_template (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(60) NOT NULL,
                    description VARCHAR(255),
                    threshold_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                    point_cost INT NOT NULL DEFAULT 0,
                    total_count INT NOT NULL DEFAULT 0,
                    issued_count INT NOT NULL DEFAULT 0,
                    validity_type VARCHAR(20) NOT NULL,
                    valid_days INT,
                    start_time DATETIME,
                    end_time DATETIME,
                    status TINYINT NOT NULL DEFAULT 1,
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        ensureTable("user_coupon", """
                CREATE TABLE user_coupon (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    template_id BIGINT NOT NULL,
                    coupon_name VARCHAR(60) NOT NULL,
                    threshold_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
                    status VARCHAR(20) NOT NULL,
                    received_time DATETIME,
                    used_time DATETIME,
                    expire_time DATETIME,
                    order_id BIGINT,
                    source_type VARCHAR(20),
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    KEY idx_coupon_user (user_id),
                    KEY idx_coupon_template (template_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        ensureTable("order_review", """
                CREATE TABLE order_review (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    user_id BIGINT NOT NULL,
                    order_id BIGINT NOT NULL,
                    drink_id BIGINT NOT NULL,
                    rating INT NOT NULL,
                    content VARCHAR(500),
                    status TINYINT NOT NULL DEFAULT 1,
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    KEY idx_review_user (user_id),
                    KEY idx_review_order (order_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        ensureTable("marketing_activity", """
                CREATE TABLE marketing_activity (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(80) NOT NULL,
                    activity_type VARCHAR(30) NOT NULL,
                    banner_image VARCHAR(255),
                    start_time DATETIME NOT NULL,
                    end_time DATETIME NOT NULL,
                    status TINYINT NOT NULL DEFAULT 1,
                    description VARCHAR(500),
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
        ensureTable("marketing_activity_rule", """
                CREATE TABLE marketing_activity_rule (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    activity_id BIGINT NOT NULL,
                    rule_type VARCHAR(30),
                    rule_value VARCHAR(255),
                    target_type VARCHAR(30),
                    target_id BIGINT,
                    sort_order INT DEFAULT 0,
                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    deleted TINYINT DEFAULT 0,
                    KEY idx_activity_rule (activity_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

        ensurePermission("points:read", "查看积分", "会员营销");
        ensurePermission("coupon:read", "查看优惠券", "会员营销");
        ensurePermission("coupon:write", "管理优惠券", "会员营销");
        ensurePermission("review:read", "查看评价", "会员营销");
        ensurePermission("review:write", "管理评价", "会员营销");
        ensurePermission("activity:read", "查看活动", "会员营销");
        ensurePermission("activity:write", "管理活动", "会员营销");

        ensureRolePermission("admin", "points:read");
        ensureRolePermission("admin", "coupon:read");
        ensureRolePermission("admin", "coupon:write");
        ensureRolePermission("admin", "review:read");
        ensureRolePermission("admin", "review:write");
        ensureRolePermission("admin", "activity:read");
        ensureRolePermission("admin", "activity:write");

        ensureRolePermission("staff", "points:read");
        ensureRolePermission("staff", "coupon:read");
        ensureRolePermission("staff", "review:read");
        ensureRolePermission("staff", "activity:read");

        ensureRolePermission("user", "points:read");
        ensureRolePermission("user", "coupon:read");
        ensureRolePermission("user", "coupon:write");
        ensureRolePermission("user", "review:read");
        ensureRolePermission("user", "review:write");
        ensureRolePermission("user", "activity:read");

        ensureSeedData();
    }

    private void ensureSeedData() {
        Integer templateCount = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM coupon_template WHERE deleted = 0", Integer.class);
        if (templateCount != null && templateCount == 0) {
            jdbcTemplate.update("""
                    INSERT INTO coupon_template
                    (name, description, threshold_amount, discount_amount, point_cost, total_count, issued_count, validity_type, valid_days, status)
                    VALUES
                    ('新人立减券', '新用户首单可用', 30.00, 8.00, 0, 500, 0, '固定天数', 15, 1),
                    ('会员满减券', '会员积分兑换专属优惠', 50.00, 12.00, 30, 300, 0, '固定天数', 10, 1)
                    """);
        }

        Integer activityCount = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM marketing_activity WHERE deleted = 0", Integer.class);
        if (activityCount != null && activityCount == 0) {
            jdbcTemplate.update("""
                    INSERT INTO marketing_activity
                    (name, activity_type, banner_image, start_time, end_time, status, description)
                    VALUES
                    ('春日猫咖打卡季', '限时促销', '', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '到店打卡、下单互动可享多重会员权益')
                    """);
            Long activityId = jdbcTemplate.queryForObject("SELECT id FROM marketing_activity WHERE name = '春日猫咖打卡季' LIMIT 1", Long.class);
            if (activityId != null) {
                jdbcTemplate.update("""
                        INSERT INTO marketing_activity_rule
                        (activity_id, rule_type, rule_value, target_type, target_id, sort_order)
                        VALUES
                        (?, '满减', '满50减12', '全场', NULL, 1),
                        (?, '积分奖励', '支付订单额等额返积分', '会员', NULL, 2)
                        """, activityId, activityId);
            }
        }
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

    private void ensureTable(String tableName, String ddl) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                """, Integer.class, tableName);
        if (count != null && count == 0) {
            jdbcTemplate.execute(ddl);
            log.info("Schema upgraded: created table {}", tableName);
        }
    }

    private void ensurePermission(String code, String name, String moduleName) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM sys_permission
                WHERE permission_code = ?
                  AND deleted = 0
                """, Integer.class, code);
        if (count != null && count == 0) {
            jdbcTemplate.update("""
                    INSERT INTO sys_permission (permission_code, permission_name, module_name, status)
                    VALUES (?, ?, ?, 1)
                    """, code, name, moduleName);
        }
    }

    private void ensureRolePermission(String roleCode, String permissionCode) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(1)
                FROM sys_role_permission rp
                INNER JOIN sys_role r ON rp.role_id = r.id AND r.deleted = 0
                INNER JOIN sys_permission p ON rp.permission_id = p.id AND p.deleted = 0
                WHERE r.role_code = ?
                  AND p.permission_code = ?
                  AND rp.deleted = 0
                """, Integer.class, roleCode, permissionCode);
        if (count != null && count == 0) {
            jdbcTemplate.update("""
                    INSERT INTO sys_role_permission (role_id, permission_id)
                    SELECT r.id, p.id
                    FROM sys_role r, sys_permission p
                    WHERE r.role_code = ?
                      AND p.permission_code = ?
                    LIMIT 1
                    """, roleCode, permissionCode);
        }
    }
}
