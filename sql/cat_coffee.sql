CREATE DATABASE IF NOT EXISTS cat_coffee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE cat_coffee;

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
    KEY idx_reservation_time (reservation_time),
    KEY idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(30) NOT NULL,
    customer_name VARCHAR(50) NOT NULL,
    reservation_id BIGINT,
    table_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    pay_status VARCHAR(20) NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
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
    status TINYINT NOT NULL DEFAULT 1,
    token_version INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username)
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

INSERT INTO reservation (customer_name, customer_phone, guest_count, reservation_time, table_id, status, note)
VALUES
('林同学', '13800000001', 2, DATE_ADD(NOW(), INTERVAL 1 DAY), 1, '待到店', '第一次来店，希望安排安静位置'),
('陈小姐', '13800000002', 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 2, '已确认', '庆生聚会');

INSERT INTO customer_order (order_no, customer_name, reservation_id, table_id, total_amount, pay_status, order_status, remark)
VALUES
('CC20260421093001', '林同学', 1, 1, 60.00, '已支付', '已完成', '加冰少糖'),
('CC20260421103002', '散客王先生', NULL, 3, 28.00, '待支付', '待制作', '打包带走');

INSERT INTO customer_order_item (order_id, drink_id, drink_name, quantity, unit_price, subtotal)
VALUES
(1, 1, '海盐拿铁', 1, 28.00, 28.00),
(1, 4, '冻干猫咪酸奶杯', 1, 22.00, 22.00),
(1, 2, '猫爪抹茶', 1, 10.00, 10.00),
(2, 1, '海盐拿铁', 1, 28.00, 28.00);

INSERT INTO sys_user (id, username, password, nickname, status, token_version)
VALUES
(1, 'admin', '$2y$10$tw0jWBKya17tGkLVJSDDteoKqp7ab6cYyyIodSgaE0BdXtOgFQ6U2', '超级管理员', 1, 0),
(2, 'staff', '$2y$10$kGKgOraSXMrFM2ewfxF/3.Bs2H0t.5.GqJU6lnR8zFBOVFDjfBRfC', '店员小猫', 1, 0),
(3, 'user', '$2a$10$4TsDEkARWur6INJCeDWt.e3PLhXFE/JK1cq.cI7eutLMMskm3JpYi', '普通用户', 1, 0);

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
(25, 'system:permission:delete', '删除权限', '系统管理', 1);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO sys_role_permission (role_id, permission_id)
VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
(1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25),
(2, 1), (2, 2), (2, 5), (2, 6), (2, 8), (2, 11), (2, 12), (2, 14), (2, 15),
(3, 2), (3, 5), (3, 8), (3, 11), (3, 12), (3, 14), (3, 15);
