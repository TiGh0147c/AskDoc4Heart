/*
 Navicat Premium Data Transfer

 Source Server         : AD4H
 Source Server Type    : MySQL
 Source Server Version : 80041
 Source Host           : localhost:3310
 Source Schema         : mydb

 Target Server Type    : MySQL
 Target Server Version : 80041
 File Encoding         : 65001

 Date: 02/04/2025 08:55:33
*/
DROP DATABASE IF EXISTS `mydb`;
CREATE DATABASE IF NOT EXISTS `mydb` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `mydb`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for Appointment
-- ----------------------------
DROP TABLE IF EXISTS Appointment;
CREATE TABLE Appointment (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_time DATETIME NOT NULL,
    appointment_status ENUM('completed', 'expired', 'cancelled', 'scheduled') NOT NULL,
    user_id INT NOT NULL,
    counselor_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_id)
);

-- ----------------------------
-- Records of Appointment
-- ----------------------------

-- ----------------------------
-- Table structure for Attendance_Record
-- ----------------------------
DROP TABLE IF EXISTS Attendance_Record;
CREATE TABLE Attendance_Record (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,  -- 考勤记录ID，主键自增
    role ENUM('counselor', 'supervisor') NOT NULL,  -- 角色：咨询师或督导
    counselor_id INT,  -- 咨询师ID（当角色为咨询师时使用）
    supervisor_id INT,  -- 督导ID（当角色为督导时使用）
    check_in_time DATETIME NOT NULL,  -- 打卡时间
    status ENUM('check_in', 'check_out') NOT NULL,  -- 上班/下班状态
    attendance_status ENUM('on_time', 'late', 'absent') NOT NULL DEFAULT 'absent',  -- 考勤状态
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_id),  -- 关联咨询师表
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id)  -- 关联督导表
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Records of Attendance_Record
-- ----------------------------

-- ----------------------------
-- Table structure for Binding_Record
-- ----------------------------
DROP TABLE IF EXISTS `Binding_Record`;
CREATE TABLE Binding_Record (
    binding_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '绑定记录ID',
    created_at DATETIME NOT NULL COMMENT '创建时间(YYYY-MM-DD HH:MM:SS)',
    binding_status ENUM('bound', 'unbound') NOT NULL COMMENT '绑定状态(bound绑定/unbound已解绑)',
    supervisor_id INT NOT NULL COMMENT '督导ID',
    counselor_id INT NOT NULL COMMENT '咨询师ID',
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id),
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Binding_Record
-- ----------------------------

-- ----------------------------
-- Table structure for Consultation_Evaluation
-- ----------------------------
DROP TABLE IF EXISTS `Counselor_Consultation_Evaluation`;
CREATE TABLE Counselor_Consultation_Evaluation (
    evaluation_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评估ID',
    psychological_state_rating TINYINT NOT NULL COMMENT '心理状态评分(1-10分)',
    emotional_stability_rating TINYINT NOT NULL COMMENT '情绪稳定性评分(1-10分)',
    communication_ability_rating TINYINT NOT NULL COMMENT '可沟通程度评分(1-10分)',
    user_visible_part TEXT COMMENT '用户可见的评价内容',
    user_invisible_part TEXT COMMENT '用户不可见的评价内容(内部使用)',
    evaluation_time DATETIME NOT NULL COMMENT '评估时间(YYYY-MM-DD HH:MM:SS)',
    session_id INT NOT NULL COMMENT '关联的会话ID',
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id),
    CHECK (psychological_state_rating BETWEEN 1 AND 10),
    CHECK (emotional_stability_rating BETWEEN 1 AND 10),
    CHECK (communication_ability_rating BETWEEN 1 AND 10)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Consultation_Evaluation
-- ----------------------------



-- ----------------------------
-- Table structure for User_Consultation_Evaluation
-- ----------------------------
DROP TABLE IF EXISTS User_Consultation_Evaluation;
CREATE TABLE User_Consultation_Evaluation (
    evaluation_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评估ID',
    evaluation_content TEXT NOT NULL COMMENT '用户评价内容',
    rating TINYINT NOT NULL COMMENT '用户评分(1-10星)',
    evaluation_time DATETIME NOT NULL COMMENT '评估时间(YYYY-MM-DD HH:MM:SS)',
    session_id INT NOT NULL COMMENT '关联的咨询会话ID',
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id),
    CHECK (rating BETWEEN 1 AND 10)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of User_Consultation_Evaluation
-- ----------------------------



-- ----------------------------
-- Table structure for Consultation_Session
-- ----------------------------
DROP TABLE IF EXISTS `Consultation_Session`;
CREATE TABLE Consultation_Session (
    session_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    session_start_time DATETIME NOT NULL COMMENT '会话开始时间(YYYY-MM-DD HH:MM:SS)',
    session_end_time DATETIME COMMENT '会话结束时间',
    session_status ENUM('in_progress', 'completed') NOT NULL DEFAULT 'in_progress' COMMENT '会话状态(进行中/已完成)',
    counselor_id INT NOT NULL COMMENT '咨询师ID',
    user_id INT NOT NULL COMMENT '用户ID',
    supervisor_id INT NOT NULL COMMENT '督导ID',
    last_message_sent_time DATETIME COMMENT '最后消息发送时间',
    user_has_evaluated BOOLEAN DEFAULT FALSE COMMENT '用户是否已评价',
    counselor_has_evaluated BOOLEAN DEFAULT FALSE COMMENT '咨询师是否已评价',
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Consultation_Session
-- ----------------------------



-- ----------------------------
-- Table structure for Session_Message
-- ----------------------------
DROP TABLE IF EXISTS Session_Message;
CREATE TABLE Session_Message (
    message_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id INT NOT NULL COMMENT '关联的会话ID',
    sender_role ENUM('user', 'counselor') NOT NULL COMMENT '发送者角色(用户/咨询师)',
    sender_id INT NOT NULL COMMENT '发送者ID(根据角色对应user_id或counselor_id)',
    message_type ENUM('text', 'image', 'file') NOT NULL DEFAULT 'text' COMMENT '消息类型',
    message_content TEXT COMMENT '消息内容(文本内容)',
    message_sent_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息发送时间',
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Session_Message
-- ----------------------------


CREATE TABLE Session_Summary (
    summary_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '摘要ID',
    session_id INT NOT NULL COMMENT '关联的会话ID',
    summary_text TEXT NOT NULL COMMENT '咨询问题摘要内容',
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id)
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;




-- ----------------------------
-- Table structure for Help_Message
-- ----------------------------
DROP TABLE IF EXISTS Help_Message;
CREATE TABLE `Help_Message`  (
   message_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '求助消息ID',
   sender_role ENUM('counselor', 'supervisor') NOT NULL COMMENT '发送者角色(咨询师/督导)',
   sender_id INT NOT NULL COMMENT '发送者ID(根据角色对应咨询师ID或督导ID)',
   message_type ENUM('text', 'image', 'file') NOT NULL DEFAULT 'text' COMMENT '消息类型',
   message_sent_time datetime NOT NULL,
   message_content text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
   help_id int NOT NULL,
   FOREIGN KEY (help_id) REFERENCES Help_Session(help_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Help_Message
-- ----------------------------

-- ----------------------------
-- Table structure for Help_Session
-- ----------------------------
DROP TABLE IF EXISTS Help_Session;
CREATE TABLE `Help_Session`  (
   help_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '求互助会话ID',
   help_start_time datetime NULL DEFAULT NULL,
   help_end_time datetime NULL DEFAULT NULL,
   help_status  enum('in_progress','completed') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'completed',
   session_id INT NOT NULL,
   FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Help_Session
-- ----------------------------

-- ----------------------------
-- Table structure for Schedule
-- ----------------------------
DROP TABLE IF EXISTS Schedule;
CREATE TABLE Schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '排班ID',
    date DATE NOT NULL COMMENT '排班日期(YYYY-MM-DD)',
    time_slot ENUM('morning', 'afternoon') NOT NULL COMMENT '时间段(上午/下午)'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Schedule
-- ----------------------------

-- ----------------------------
-- Table structure for Supervisor_Schedule
-- ----------------------------
DROP TABLE IF EXISTS Supervisor_Schedule;
CREATE TABLE Supervisor_Schedule  (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  schedule_id INT NOT NULL COMMENT '排班ID',
  supervisor_id INT NOT NULL COMMENT '督导ID',
  FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id),
  FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Supervisor_Schedule
-- ----------------------------
-- ----------------------------
-- Table structure for Counselor_Schedule
-- ----------------------------
DROP TABLE IF EXISTS `Counselor_Schedule`;
CREATE TABLE `Counselor_Schedule`  (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  schedule_id INT NOT NULL COMMENT '排班ID',
  counselor_id INT NOT NULL COMMENT '咨询师ID',
  FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id),
  FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor_Schedule
-- ----------------------------

-- ----------------------------
-- Table structure for Leave_Application
-- ----------------------------
DROP TABLE IF EXISTS `Leave_Application`;
CREATE TABLE Leave_Application (
    leave_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '请假ID',
    leave_reason TEXT NOT NULL COMMENT '请假原因',
    leave_status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending' COMMENT '审批状态(待定/已批准/已拒绝)',
    application_date date NOT NULL COMMENT '申请日期(YYYY-MM-DD )',
    application_time ENUM('morning','afternoon') NOT NULL COMMENT '申请时间段(上午/下午)',
    role ENUM('counselor','supervisor') NOT NULL COMMENT '请假角色(咨询师/督导)',
    applicant_id INT NOT NULL COMMENT '申请者ID(咨询师ID或督导ID)',
    schedule_id INT COMMENT '关联的排班ID',
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Leave_Application
-- ----------------------------

-- ----------------------------
-- Table structure for Queue
-- ----------------------------
DROP TABLE IF EXISTS Queue;
CREATE TABLE Queue (
    queue_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '排队ID',
    queue_number INT NOT NULL COMMENT '队列序号',
    user_number VARCHAR(20) COMMENT '用户排队编号(可选)',
    join_queue_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入队列时间',
    exit_queue_time DATETIME COMMENT '离开队列时间',
    queue_status ENUM('waiting', 'completed', 'cancelled') NOT NULL DEFAULT 'waiting' COMMENT '排队状态(等待中/已完成/已取消)',
    user_id INT NOT NULL COMMENT '用户ID',
    counselor_id INT NOT NULL COMMENT '咨询师ID',
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Queue
-- ----------------------------


-- ----------------------------
-- Table structure for Counselor
-- ----------------------------
DROP TABLE IF EXISTS Counselor;
CREATE TABLE Counselor  (
   counselor_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '咨询师ID',
   name varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
   avatar varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   phone_number varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   password varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
   email varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   gender enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   counselor_certificate text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
   is_supervisor tinyint(1) NULL DEFAULT 0,
   status enum('available', 'unavailable') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'available',
   expertise_area varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   on_duty tinyint(1) NULL DEFAULT 0
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor
-- ----------------------------
INSERT INTO `Counselor` VALUES (1, '张心理咨询师', '2ecaew','13800138001', 'hashed_password_1', 'counselor1@example.com', male,NULL, 0, 'available', '焦虑症', 0);
INSERT INTO `Counselor` VALUES (2, '李督导','2fwefv','13800138002','hashed_password_2', 'supervisor1@example.com', female,NULL, 1, 'available', '抑郁症', 0);



-- ----------------------------
-- Table structure for Supervisor
-- ----------------------------
DROP TABLE IF EXISTS Supervisor;
CREATE TABLE Supervisor  (
   supervisor_id int PRIMARY KEY AUTO_INCREMENT COMMENT '督导ID',
   name varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
   phone_number varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   avatar varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   password varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
   gender enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   email varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   counselor_certificate text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
   is_supervisor tinyint(1) NULL DEFAULT 1,
   status enum('available', 'unavailable') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'available',
   expertise_area varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   on_duty tinyint(1) NULL DEFAULT 0
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Supervisor
-- ----------------------------
INSERT INTO Supervisor VALUES (1, '王督导', '13800138003','swfvv' ,'hashed_password_3', 'supervisor2@example.com',male, NULL, 1, 'available', '家庭关系', 0);

-- ----------------------------
-- Table structure for User
-- ----------------------------
DROP TABLE IF EXISTS User;
CREATE TABLE User  (
   user_id int PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
   union_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   open_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   nickname varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   avatar varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   gender enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   birthday date NULL DEFAULT NULL,
   email varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   password varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
   occupation varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
   phone_number varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS User_Modification_Audit;
CREATE TABLE User_Modification_Audit (
    audit_id int PRIMARY KEY AUTO_INCREMENT COMMENT '修改ID',
    user_id int NOT NULL COMMENT '用户ID',
    modify_field enum('nickname','avatar') NOT NULL COMMENT '修改项',
    new_value varchar(255) NOT NULL COMMENT '新内容',
    audit_status enum('pending','approved','rejected') NOT NULL DEFAULT 'pending' COMMENT '审核状态',
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of User
-- ----------------------------



DROP TABLE IF EXISTS Administrator;
CREATE TABLE Administrator (
    administrator_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名(登录账号)',
    password VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    contact_number VARCHAR(20) COMMENT '联系电话',
    avatar VARCHAR(255) COMMENT '头像URL',
    status ENUM('active', 'disabled') DEFAULT 'active' COMMENT '状态(启用/禁用)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

