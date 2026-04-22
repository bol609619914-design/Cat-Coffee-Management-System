CREATE DATABASE IF NOT EXISTS cat_coffee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE cat_coffee;

DROP TABLE IF EXISTS marketing_activity_rule;
DROP TABLE IF EXISTS marketing_activity;
DROP TABLE IF EXISTS order_review;
DROP TABLE IF EXISTS user_coupon;
DROP TABLE IF EXISTS coupon_template;
DROP TABLE IF EXISTS member_point_flow;
DROP TABLE IF EXISTS customer_order_item;
DROP TABLE IF EXISTS customer_order;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS cafe_table;
DROP TABLE IF EXISTS drink;
DROP TABLE IF EXISTS cat;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE cat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    breed VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    health_status VARCHAR(30) NOT NULL,
    personality_tag VARCHAR(100),
    adoption_status VARCHAR(30),
    feeding_cost DECIMAL(10, 2) DEFAULT 0,
    birthday DATE,
    avatar VARCHAR(255),
    introduction VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE drink (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    category VARCHAR(30) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    sales INT NOT NULL DEFAULT 0,
    is_recommended TINYINT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    description VARCHAR(300),
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cafe_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_no VARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    area_name VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    customer_name VARCHAR(50) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    guest_count INT NOT NULL,
    reservation_time DATETIME NOT NULL,
    table_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    note VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_reservation_user_id (user_id),
    KEY idx_reservation_time (reservation_time),
    KEY idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    order_no VARCHAR(30) NOT NULL,
    customer_name VARCHAR(50) NOT NULL,
    reservation_id BIGINT,
    table_id BIGINT NOT NULL,
    original_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    payable_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    pay_status VARCHAR(20) NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    points_used INT NOT NULL DEFAULT 0,
    points_awarded INT NOT NULL DEFAULT 0,
    user_coupon_id BIGINT,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_user_id (user_id),
    KEY idx_table_order (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    drink_id BIGINT NOT NULL,
    drink_name VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    member_points INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    token_version INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE coupon_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL,
    description VARCHAR(255),
    threshold_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    coupon_name VARCHAR(60) NOT NULL,
    threshold_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(50) NOT NULL,
    permission_name VARCHAR(50) NOT NULL,
    module_name VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_user_role_user (user_id),
    KEY idx_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_role_permission_role (role_id),
    KEY idx_role_permission_permission (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO cat (name, breed, age, gender, health_status, personality_tag, adoption_status, feeding_cost, birthday, avatar, introduction)
VALUES
('奶盖', '英短', 2, '母', '健康', '亲人,爱趴窗边', '店内营业', 380.00, '2024-01-02', 'https://images.unsplash.com/photo-1511044568932-338cba0ad803', '店里的明星接待猫，特别喜欢晒太阳。'),
('奥利奥', '奶牛猫', 3, '公', '健康', '活泼,好奇', '可领养', 420.00, '2023-08-18', 'https://images.unsplash.com/photo-1519052537078-e6302a4968d4', '社牛型猫咪，很喜欢和客人互动。'),
('布丁', '布偶猫', 1, '母', '观察中', '安静,温柔', '店内营业', 560.00, '2025-01-09', 'https://images.unsplash.com/photo-1513245543132-31f507417b26', '性格很稳，适合新手客人。');

INSERT INTO drink (name, category, price, stock, sales, is_recommended, image_url, description, status)
VALUES
('海盐拿铁', '咖啡', 28.00, 120, 56, 1, '', '店内招牌咖啡，口感顺滑。', 1),
('猫爪抹茶', '特调', 32.00, 80, 41, 1, '', '顶部拉花为猫爪造型，拍照很出片。', 1),
('蜜桃气泡美式', '冷饮', 30.00, 60, 38, 0, '', '偏清爽路线，适合夏季。', 1),
('冻干猫咪酸奶杯', '甜品', 22.00, 45, 25, 1, '', '周边联动甜品，年轻用户喜欢。', 1);

INSERT INTO cafe_table (table_no, capacity, area_name, status, remark)
VALUES
('A1', 2, '落地窗区', '空闲', '靠近猫爬架'),
('A2', 4, '落地窗区', '已预订', '适合小团体'),
('B1', 2, '安静区', '空闲', '适合办公'),
('C1', 6, '活动区', '清洁中', '适合生日活动');

INSERT INTO reservation (user_id, customer_name, customer_phone, guest_count, reservation_time, table_id, status, note)
VALUES
(3, '普通用户', '13800000003', 2, DATE_ADD(NOW(), INTERVAL 1 DAY), 1, '待到店', '线上预约，希望安排安静位置'),
(NULL, '陈小姐', '13800000002', 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 2, '已确认', '庆生聚会');

INSERT INTO customer_order (user_id, order_no, customer_name, reservation_id, table_id, original_amount, total_amount, discount_amount, payable_amount, pay_status, order_status, points_used, points_awarded, user_coupon_id, remark)
VALUES
(3, 'CC20260421093001', '普通用户', 1, 1, 60.00, 52.00, 8.00, 52.00, '已支付', '已完成', 0, 52, 1, '加冰少糖'),
(NULL, 'CC20260421103002', '散客王先生', NULL, 3, 28.00, 28.00, 0.00, 28.00, '待支付', '待制作', 0, 0, NULL, '打包带走');

INSERT INTO customer_order_item (order_id, drink_id, drink_name, quantity, unit_price, subtotal)
VALUES
(1, 1, '海盐拿铁', 1, 28.00, 28.00),
(1, 4, '冻干猫咪酸奶杯', 1, 22.00, 22.00),
(1, 2, '猫爪抹茶', 1, 10.00, 10.00),
(2, 1, '海盐拿铁', 1, 28.00, 28.00);

INSERT INTO sys_user (id, username, password, nickname, member_points, status, token_version)
VALUES
(1, 'admin', '$2y$10$tw0jWBKya17tGkLVJSDDteoKqp7ab6cYyyIodSgaE0BdXtOgFQ6U2', '超级管理员', 200, 1, 0),
(2, 'staff', '$2y$10$kGKgOraSXMrFM2ewfxF/3.Bs2H0t.5.GqJU6lnR8zFBOVFDjfBRfC', '店员小猫', 80, 1, 0),
(3, 'user', '$2a$10$4TsDEkARWur6INJCeDWt.e3PLhXFE/JK1cq.cI7eutLMMskm3JpYi', '普通用户', 128, 1, 0);

INSERT INTO sys_role (id, role_code, role_name, status)
VALUES
(1, 'admin', '管理员', 1),
(2, 'staff', '店员', 1),
(3, 'user', '普通用户', 1);

INSERT INTO sys_permission (id, permission_code, permission_name, module_name, status)
VALUES
(1, 'dashboard:view', '查看经营看板', '经营看板', 1),
(2, 'cat:read', '查看猫咪', '猫咪管理', 1),
(3, 'cat:write', '编辑猫咪', '猫咪管理', 1),
(4, 'cat:delete', '删除猫咪', '猫咪管理', 1),
(5, 'drink:read', '查看饮品', '饮品管理', 1),
(6, 'drink:write', '编辑饮品', '饮品管理', 1),
(7, 'drink:delete', '删除饮品', '饮品管理', 1),
(8, 'table:read', '查看桌台', '桌台管理', 1),
(9, 'table:write', '编辑桌台', '桌台管理', 1),
(10, 'table:delete', '删除桌台', '桌台管理', 1),
(11, 'reservation:read', '查看预约', '预约管理', 1),
(12, 'reservation:write', '编辑预约', '预约管理', 1),
(13, 'reservation:delete', '删除预约', '预约管理', 1),
(14, 'order:read', '查看订单', '订单管理', 1),
(15, 'order:write', '编辑订单', '订单管理', 1),
(16, 'order:delete', '删除订单', '订单管理', 1),
(17, 'system:user:read', '查看用户', '系统管理', 1),
(18, 'system:user:write', '编辑用户', '系统管理', 1),
(19, 'system:user:delete', '删除用户', '系统管理', 1),
(20, 'system:role:read', '查看角色', '系统管理', 1),
(21, 'system:role:write', '编辑角色', '系统管理', 1),
(22, 'system:role:delete', '删除角色', '系统管理', 1),
(23, 'system:permission:read', '查看权限', '系统管理', 1),
(24, 'system:permission:write', '编辑权限', '系统管理', 1),
(25, 'system:permission:delete', '删除权限', '系统管理', 1),
(26, 'points:read', '查看积分', '会员营销', 1),
(27, 'coupon:read', '查看优惠券', '会员营销', 1),
(28, 'coupon:write', '管理优惠券', '会员营销', 1),
(29, 'review:read', '查看评价', '会员营销', 1),
(30, 'review:write', '管理评价', '会员营销', 1),
(31, 'activity:read', '查看活动', '会员营销', 1),
(32, 'activity:write', '管理活动', '会员营销', 1);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO member_point_flow (user_id, change_type, change_amount, balance_after, biz_type, biz_id, remark)
VALUES
(3, '发放', 60, 60, '订单', 1, '订单支付奖励积分'),
(3, '兑换', -30, 30, '优惠券', 2, '积分兑换会员满减券'),
(3, '发放', 98, 128, '活动', 1, '春日猫咖打卡季活动奖励');

INSERT INTO coupon_template (id, name, description, threshold_amount, discount_amount, point_cost, total_count, issued_count, validity_type, valid_days, start_time, end_time, status)
VALUES
(1, '新人立减券', '新用户首单可用', 30.00, 8.00, 0, 500, 1, '固定天数', 15, NULL, NULL, 1),
(2, '会员满减券', '会员积分兑换专属优惠', 50.00, 12.00, 30, 300, 1, '固定天数', 10, NULL, NULL, 1);

INSERT INTO user_coupon (id, user_id, template_id, coupon_name, threshold_amount, discount_amount, status, received_time, used_time, expire_time, order_id, source_type)
VALUES
(1, 3, 1, '新人立减券', 30.00, 8.00, '已使用', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 1, '后台发放'),
(2, 3, 2, '会员满减券', 50.00, 12.00, '未使用', NOW(), NULL, DATE_ADD(NOW(), INTERVAL 9 DAY), NULL, '积分兑换');

INSERT INTO order_review (user_id, order_id, drink_id, rating, content, status)
VALUES
(3, 1, 1, 5, '海盐拿铁很顺口，拉花也很好看。', 1),
(3, 1, 4, 4, '酸奶杯拍照很好看，口感也清爽。', 1);

INSERT INTO marketing_activity (id, name, activity_type, banner_image, start_time, end_time, status, description)
VALUES
(1, '春日猫咖打卡季', '限时促销', '', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, '到店打卡、下单互动可享多重会员权益'),
(2, '会员双倍积分周', '会员拉新', '', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 1, '活动期间支付订单可累计双倍积分。');

INSERT INTO marketing_activity_rule (activity_id, rule_type, rule_value, target_type, target_id, sort_order)
VALUES
(1, '满减', '满50减12', '全场', NULL, 1),
(1, '积分奖励', '支付订单额等额返积分', '会员', NULL, 2),
(2, '积分奖励', '全单双倍积分', '会员', NULL, 1);

INSERT INTO sys_role_permission (role_id, permission_id)
VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
(1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30), (1, 31), (1, 32),
(2, 1), (2, 2), (2, 5), (2, 6), (2, 8), (2, 11), (2, 12), (2, 14), (2, 15), (2, 26), (2, 27), (2, 29), (2, 31),
(3, 2), (3, 5), (3, 8), (3, 11), (3, 12), (3, 14), (3, 15), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30), (3, 31);
