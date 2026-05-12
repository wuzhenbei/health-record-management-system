-- 社区医疗健康档案管理系统 - 数据库初始化脚本
-- 字符集：utf8mb4，引擎：InnoDB，时区：Asia/Shanghai

CREATE DATABASE IF NOT EXISTS community_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE community_health;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
  `role`        VARCHAR(20)  NOT NULL COMMENT '角色：ADMIN/DOCTOR/RESIDENT',
  `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `avatar`      VARCHAR(200) DEFAULT NULL COMMENT '头像URL',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 居民档案表
-- ----------------------------
DROP TABLE IF EXISTS `resident`;
CREATE TABLE `resident` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       DEFAULT NULL COMMENT '关联sys_user.id（居民账号）',
  `name`        VARCHAR(50)  NOT NULL COMMENT '姓名',
  `id_card`     VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
  `gender`      TINYINT      DEFAULT NULL COMMENT '性别：0女 1男',
  `birth_date`  DATE         DEFAULT NULL COMMENT '出生日期',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `address`     VARCHAR(200) DEFAULT NULL COMMENT '家庭住址',
  `blood_type`  VARCHAR(10)  DEFAULT NULL COMMENT '血型：A/B/O/AB',
  `marriage`    VARCHAR(10)  DEFAULT NULL COMMENT '婚姻状况',
  `occupation`  VARCHAR(50)  DEFAULT NULL COMMENT '职业',
  `allergy`     VARCHAR(200) DEFAULT NULL COMMENT '过敏史',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='居民档案表';

-- ----------------------------
-- 健康指标（体检）表
-- ----------------------------
DROP TABLE IF EXISTS `health_indicator`;
CREATE TABLE `health_indicator` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resident_id`  BIGINT       NOT NULL COMMENT '居民ID',
  `measure_date` DATE         NOT NULL COMMENT '检测日期',
  `height`       DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
  `weight`       DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
  `bmi`          DECIMAL(4,1) DEFAULT NULL COMMENT 'BMI',
  `systolic`     INT          DEFAULT NULL COMMENT '收缩压(mmHg)',
  `diastolic`    INT          DEFAULT NULL COMMENT '舒张压(mmHg)',
  `heart_rate`   INT          DEFAULT NULL COMMENT '心率(次/分)',
  `blood_sugar`  DECIMAL(4,1) DEFAULT NULL COMMENT '血糖(mmol/L)',
  `cholesterol`  DECIMAL(4,1) DEFAULT NULL COMMENT '胆固醇(mmol/L)',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_measure_date` (`measure_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康指标表';

-- ----------------------------
-- 病史/就诊记录表
-- ----------------------------
DROP TABLE IF EXISTS `medical_history`;
CREATE TABLE `medical_history` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resident_id` BIGINT       NOT NULL COMMENT '居民ID',
  `visit_date`  DATE         NOT NULL COMMENT '就诊日期',
  `disease`     VARCHAR(100) NOT NULL COMMENT '疾病名称',
  `diagnosis`   VARCHAR(500) DEFAULT NULL COMMENT '诊断描述',
  `treatment`   VARCHAR(500) DEFAULT NULL COMMENT '治疗方案',
  `doctor_id`   BIGINT       DEFAULT NULL COMMENT '接诊医生ID',
  `doctor_name` VARCHAR(50)  DEFAULT NULL COMMENT '接诊医生姓名',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resident_id` (`resident_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病史/就诊记录表';

-- ----------------------------
-- 慢病登记表
-- ----------------------------
DROP TABLE IF EXISTS `chronic_disease`;
CREATE TABLE `chronic_disease` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resident_id`  BIGINT      NOT NULL COMMENT '居民ID',
  `disease_type` VARCHAR(50) NOT NULL COMMENT '病种：高血压/糖尿病/冠心病/脑卒中/COPD/其他',
  `confirm_date` DATE        DEFAULT NULL COMMENT '确诊日期',
  `status`       VARCHAR(20) DEFAULT '管理中' COMMENT '状态：管理中/已治愈/死亡/迁出',
  `level`        VARCHAR(20) DEFAULT NULL COMMENT '分级：一级/二级/三级（高血压）',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted`      TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_disease_type` (`disease_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='慢病登记表';

-- ----------------------------
-- 随访记录表
-- ----------------------------
DROP TABLE IF EXISTS `followup_record`;
CREATE TABLE `followup_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resident_id`   BIGINT       NOT NULL COMMENT '居民ID',
  `chronic_id`    BIGINT       DEFAULT NULL COMMENT '慢病ID',
  `followup_date` DATE         NOT NULL COMMENT '随访日期',
  `doctor_id`     BIGINT       DEFAULT NULL COMMENT '随访医生ID',
  `doctor_name`   VARCHAR(50)  DEFAULT NULL COMMENT '随访医生姓名',
  `followup_type` VARCHAR(20)  DEFAULT '门诊' COMMENT '随访方式：门诊/电话/入户',
  `bp`            VARCHAR(20)  DEFAULT NULL COMMENT '血压（如120/80）',
  `blood_sugar`   DECIMAL(4,1) DEFAULT NULL COMMENT '血糖(mmol/L)',
  `symptom`       VARCHAR(500) DEFAULT NULL COMMENT '症状描述',
  `advice`        VARCHAR(500) DEFAULT NULL COMMENT '医嘱/处置意见',
  `next_date`     DATE         DEFAULT NULL COMMENT '下次随访日期',
  `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_followup_date` (`followup_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访记录表';

-- 健康预警表
CREATE TABLE IF NOT EXISTS `health_alert` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `resident_id`  BIGINT       NOT NULL COMMENT '居民ID',
  `indicator_id` BIGINT       DEFAULT NULL COMMENT '关联体检记录ID',
  `alert_type`   VARCHAR(50)  NOT NULL COMMENT '预警类型：高血压/高血糖/超重/心率异常',
  `alert_value`  VARCHAR(100) NOT NULL COMMENT '异常指标值描述',
  `measure_date` DATE         DEFAULT NULL COMMENT '检测日期',
  `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '0-待处理 1-已处理',
  `handle_note`  VARCHAR(200) DEFAULT NULL COMMENT '处理备注',
  `deleted`      TINYINT      NOT NULL DEFAULT 0,
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康预警表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT,
  `user_id`      BIGINT        DEFAULT NULL COMMENT '操作人ID',
  `username`     VARCHAR(50)   DEFAULT NULL COMMENT '操作人账号',
  `real_name`    VARCHAR(50)   DEFAULT NULL COMMENT '操作人姓名',
  `module`       VARCHAR(50)   DEFAULT NULL COMMENT '模块名称',
  `op_type`      VARCHAR(20)   DEFAULT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE',
  `description`  VARCHAR(200)  DEFAULT NULL COMMENT '操作描述',
  `request_uri`  VARCHAR(200)  DEFAULT NULL COMMENT '请求路径',
  `request_method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法',
  `ip`           VARCHAR(50)   DEFAULT NULL COMMENT '操作IP',
  `cost_time`    BIGINT        DEFAULT NULL COMMENT '耗时(ms)',
  `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 初始化示例数据
-- ----------------------------

-- 系统用户（密码均为 123456，BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `role`, `real_name`, `phone`, `status`) VALUES
('admin',      '$2a$10$ixlPY3AAd4Ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWdhzlbU0qa86', 'ADMIN',    '系统管理员', '13800000001', 1),
('doctor01',   '$2a$10$ixlPY3AAd4Ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWdhzlbU0qa86', 'DOCTOR',   '张医生',    '13800000002', 1),
('doctor02',   '$2a$10$ixlPY3AAd4Ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWdhzlbU0qa86', 'DOCTOR',   '李医生',    '13800000003', 1),
('resident01', '$2a$10$ixlPY3AAd4Ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWdhzlbU0qa86', 'RESIDENT', '王建国',    '13900000001', 1),
('resident02', '$2a$10$ixlPY3AAd4Ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWdhzlbU0qa86', 'RESIDENT', '李秀英',    '13900000002', 1),
('resident03', '$2a$10$ixlPY3AAd4Ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWdhzlbU0qa86', 'RESIDENT', '陈明',      '13900000003', 1);

-- 居民档案
INSERT INTO `resident` (`user_id`, `name`, `id_card`, `gender`, `birth_date`, `phone`, `address`, `blood_type`, `marriage`, `occupation`, `allergy`) VALUES
(4, '王建国', '110101195801011234', 1, '1958-01-01', '13900000001', '北京市朝阳区幸福里1号', 'A',  '已婚', '退休',   '青霉素'),
(5, '李秀英', '110101196203052345', 0, '1962-03-05', '13900000002', '北京市朝阳区幸福里2号', 'O',  '已婚', '退休',   '无'),
(6, '陈明',   '110101197005123456', 1, '1970-05-12', '13900000003', '北京市朝阳区幸福里3号', 'B',  '已婚', '工人',   '无'),
(NULL, '刘桂花', '110101195505254567', 0, '1955-05-25', '13900000004', '北京市朝阳区幸福里4号', 'AB', '已婚', '退休',   '磺胺类'),
(NULL, '赵志远', '110101198007155678', 1, '1980-07-15', '13900000005', '北京市朝阳区幸福里5号', 'A',  '已婚', '工程师', '无'),
(NULL, '孙玉兰', '110101196811246789', 0, '1968-11-24', '13900000006', '北京市朝阳区幸福里6号', 'O',  '离异', '教师',   '无'),
(NULL, '周大勇', '110101195210307890', 1, '1952-10-30', '13900000007', '北京市朝阳区幸福里7号', 'A',  '已婚', '退休',   '无'),
(NULL, '吴美玲', '110101197604128901', 0, '1976-04-12', '13900000008', '北京市朝阳区幸福里8号', 'B',  '已婚', '护士',   '阿司匹林'),
(NULL, '郑国强', '110101196109189012', 1, '1961-09-18', '13900000009', '北京市朝阳区幸福里9号', 'O',  '已婚', '退休',   '无'),
(NULL, '黄小燕', '110101197302270123', 0, '1973-02-27', '13900000010', '北京市朝阳区幸福里10号','A',  '已婚', '会计',   '无');

-- 健康指标
INSERT INTO `health_indicator` (`resident_id`, `measure_date`, `height`, `weight`, `bmi`, `systolic`, `diastolic`, `heart_rate`, `blood_sugar`, `cholesterol`) VALUES
(1, '2024-01-10', 170.0, 72.0, 24.9, 145, 92, 78, 5.8, 5.2),
(1, '2024-04-10', 170.0, 71.5, 24.7, 142, 90, 76, 5.6, 5.1),
(1, '2024-07-10', 170.0, 71.0, 24.6, 138, 88, 75, 5.5, 5.0),
(2, '2024-02-15', 158.0, 63.0, 25.2, 130, 82, 72, 7.2, 5.8),
(2, '2024-05-15', 158.0, 62.5, 25.0, 128, 80, 71, 7.0, 5.6),
(3, '2024-03-20', 175.0, 80.0, 26.1, 125, 78, 80, 5.2, 4.8),
(4, '2024-01-25', 155.0, 68.0, 28.3, 155, 96, 82, 9.5, 6.2),
(4, '2024-04-25', 155.0, 67.0, 27.9, 152, 94, 80, 9.2, 6.0),
(5, '2024-02-10', 172.0, 75.0, 25.3, 122, 76, 68, 5.0, 4.5),
(6, '2024-03-15', 162.0, 65.0, 24.8, 135, 85, 74, 6.8, 5.4),
(7, '2024-01-05', 168.0, 78.0, 27.6, 162, 100, 85, 11.2, 7.1),
(8, '2024-02-20', 160.0, 58.0, 22.7, 118, 74, 70, 4.9, 4.2),
(9, '2024-03-08', 173.0, 82.0, 27.4, 148, 95, 82, 8.6, 6.5),
(10, '2024-04-18', 163.0, 60.0, 22.6, 120, 75, 72, 5.1, 4.6);

-- 慢病登记
INSERT INTO `chronic_disease` (`resident_id`, `disease_type`, `confirm_date`, `status`, `level`) VALUES
(1, '高血压', '2020-06-01', '管理中', '二级'),
(2, '糖尿病', '2019-03-15', '管理中', NULL),
(2, '高血压', '2021-08-20', '管理中', '一级'),
(4, '糖尿病', '2018-11-10', '管理中', NULL),
(4, '高血压', '2018-11-10', '管理中', '三级'),
(6, '高血压', '2022-01-05', '管理中', '一级'),
(7, '高血压', '2017-09-12', '管理中', '三级'),
(7, '糖尿病', '2019-05-20', '管理中', NULL),
(7, '冠心病', '2020-12-08', '管理中', NULL),
(9, '糖尿病', '2021-04-16', '管理中', NULL),
(9, '高血压', '2021-04-16', '管理中', '二级'),
(3, '冠心病', '2023-02-14', '管理中', NULL),
(5, '脑卒中', '2022-07-30', '管理中', NULL),
(10, 'COPD', '2020-10-22', '管理中', NULL);

-- 病史记录
INSERT INTO `medical_history` (`resident_id`, `visit_date`, `disease`, `diagnosis`, `treatment`, `doctor_id`, `doctor_name`) VALUES
(1, '2020-06-01', '高血压', '原发性高血压2级', '硝苯地平缓释片 20mg qd', 2, '张医生'),
(1, '2023-08-15', '高血压随诊', '血压控制尚可', '继续当前用药，加强运动', 2, '张医生'),
(2, '2019-03-15', '糖尿病', '2型糖尿病', '二甲双胍 0.5g tid', 2, '张医生'),
(2, '2021-08-20', '高血压', '原发性高血压1级', '氨氯地平 5mg qd', 3, '李医生'),
(3, '2023-02-14', '冠心病', '稳定型心绞痛', '阿司匹林100mg qd，硝酸异山梨酯10mg tid', 3, '李医生'),
(4, '2018-11-10', '糖尿病+高血压', '2型糖尿病合并高血压3级', '胰岛素强化治疗+苯磺酸左氨氯地平', 2, '张医生'),
(7, '2017-09-12', '高血压', '高血压3级极高危', '多种降压药联合治疗', 2, '张医生'),
(7, '2020-12-08', '冠心病', '急性心肌梗死恢复期', '抗血小板+他汀类药物长期维持', 3, '李医生'),
(9, '2021-04-16', '糖尿病', '2型糖尿病', '格列美脲+二甲双胍', 3, '李医生');

-- 随访记录
INSERT INTO `followup_record` (`resident_id`, `chronic_id`, `followup_date`, `doctor_id`, `doctor_name`, `followup_type`, `bp`, `blood_sugar`, `symptom`, `advice`, `next_date`) VALUES
(1, 1, '2024-01-10', 2, '张医生', '门诊', '145/92', NULL, '偶有头痛', '监测血压，低盐饮食，按时服药', '2024-04-10'),
(1, 1, '2024-04-10', 2, '张医生', '门诊', '142/90', NULL, '无明显不适', '继续用药，加强运动', '2024-07-10'),
(1, 1, '2024-07-10', 2, '张医生', '门诊', '138/88', NULL, '无不适',   '血压控制好，维持用药', '2024-10-10'),
(2, 2, '2024-02-15', 2, '张医生', '门诊', '130/82', 7.2, '口渴、乏力', '控制饮食，增加运动，按时用药', '2024-05-15'),
(2, 2, '2024-05-15', 3, '李医生', '电话', '128/80', 7.0, '症状有所缓解', '维持用药方案', '2024-08-15'),
(4, 5, '2024-01-25', 2, '张医生', '门诊', '155/96', 9.5, '头晕、视物模糊', '调整降压方案，血糖控制不佳需加量', '2024-04-25'),
(4, 5, '2024-04-25', 2, '张医生', '门诊', '152/94', 9.2, '头晕减轻', '继续调整用药', '2024-07-25'),
(7, 7, '2024-01-05', 3, '李医生', '入户', '162/100', 11.2, '头痛、胸闷', '紧急调整用药，建议住院观察', '2024-02-05'),
(9, 10, '2024-03-08', 3, '李医生', '门诊', '148/95', 8.6, '多饮多尿', '增加用药剂量，低糖饮食', '2024-06-08');
