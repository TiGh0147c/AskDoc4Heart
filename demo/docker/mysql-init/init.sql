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
DROP TABLE IF EXISTS `Appointment`;
CREATE TABLE `Appointment`  (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `appointment_time` datetime NOT NULL,
  `appointment_status` enum('pending','confirmed','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `user_Id` int NOT NULL,
  `counselor_Id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`appointment_id`) USING BTREE,
  INDEX `user_Id`(`user_Id` ASC) USING BTREE,
  INDEX `counselor_Id`(`counselor_Id` ASC) USING BTREE,
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`user_Id`) REFERENCES `User` (`user_Id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Appointment
-- ----------------------------

-- ----------------------------
-- Table structure for Attendance_Record
-- ----------------------------
DROP TABLE IF EXISTS `Attendance_Record`;
CREATE TABLE `Attendance_Record`  (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `clock_in_time` datetime NULL DEFAULT NULL,
  `clock_out_time` datetime NULL DEFAULT NULL,
  `attendance_status` enum('present','late','absent','on_leave') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'absent',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`attendance_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Attendance_Record
-- ----------------------------

-- ----------------------------
-- Table structure for Binding_Record
-- ----------------------------
DROP TABLE IF EXISTS `Binding_Record`;
CREATE TABLE `Binding_Record`  (
  `binding_id` int NOT NULL AUTO_INCREMENT,
  `binding_date` date NOT NULL,
  `binding_status` enum('active','inactive','terminated') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active',
  `supervisor_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`binding_id`) USING BTREE,
  UNIQUE INDEX `supervisor_id`(`supervisor_id` ASC, `counselor_id` ASC) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `binding_record_ibfk_1` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `binding_record_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Binding_Record
-- ----------------------------

-- ----------------------------
-- Table structure for Consultation_Evaluation
-- ----------------------------
DROP TABLE IF EXISTS `Consultation_Evaluation`;
CREATE TABLE `Consultation_Evaluation`  (
  `evaluation_Id` int NOT NULL AUTO_INCREMENT,
  `user_visible_part` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `user_invisible_part` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `evaluation_time` datetime NOT NULL,
  `rating` int NULL DEFAULT NULL,
  `session_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`evaluation_Id`) USING BTREE,
  INDEX `session_id`(`session_id` ASC) USING BTREE,
  CONSTRAINT `consultation_evaluation_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `Consultation_Session` (`session_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `consultation_evaluation_chk_1` CHECK (`rating` between 1 and 5)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Consultation_Evaluation
-- ----------------------------

-- ----------------------------
-- Table structure for Consultation_History
-- ----------------------------
DROP TABLE IF EXISTS `Consultation_History`;
CREATE TABLE `Consultation_History`  (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `session_start_time` datetime NOT NULL,
  `session_end_time` datetime NOT NULL,
  `rating` int NULL DEFAULT NULL,
  `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `help_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `session_id` int NOT NULL,
  `evaluation_id` int NULL DEFAULT NULL,
  `user_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`history_id`) USING BTREE,
  INDEX `session_id`(`session_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `consultation_history_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `Consultation_Session` (`session_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `consultation_history_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `consultation_history_ibfk_3` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `consultation_history_chk_1` CHECK (`rating` between 1 and 5)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Consultation_History
-- ----------------------------

-- ----------------------------
-- Table structure for Consultation_Session
-- ----------------------------
DROP TABLE IF EXISTS `Consultation_Session`;
CREATE TABLE `Consultation_Session`  (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `session_start_time` datetime NULL DEFAULT NULL,
  `session_end_time` datetime NULL DEFAULT NULL,
  `session_status` enum('scheduled','in_progress','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'scheduled',
  `last_message_sent_time` datetime NULL DEFAULT NULL,
  `counselor_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `consultation_session_ibfk_1` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Consultation_Session
-- ----------------------------

-- ----------------------------
-- Table structure for Counselor
-- ----------------------------
DROP TABLE IF EXISTS `Counselor`;
CREATE TABLE `Counselor`  (
  `counselor_Id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `counselor_certificate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `is_supervisor` tinyint(1) NULL DEFAULT 0,
  `status` enum('active','inactive','on_leave') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active',
  `expertise_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `on_duty` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`counselor_Id`) USING BTREE,
  UNIQUE INDEX `phone_number`(`phone_number` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor
-- ----------------------------
INSERT INTO `Counselor` VALUES (1, '张心理咨询师', '13800138001', 'hashed_password_1', 'counselor1@example.com', NULL, 0, 'active', '焦虑症', 0, '2025-03-31 05:05:20', '2025-03-31 05:05:20');
INSERT INTO `Counselor` VALUES (2, '李督导', '13800138002', 'hashed_password_2', 'supervisor1@example.com', NULL, 1, 'active', '抑郁症', 0, '2025-03-31 05:05:20', '2025-03-31 05:05:20');

-- ----------------------------
-- Table structure for Counselor_Attendance
-- ----------------------------
DROP TABLE IF EXISTS `Counselor_Attendance`;
CREATE TABLE `Counselor_Attendance`  (
  `attendance_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  PRIMARY KEY (`attendance_id`, `counselor_id`) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `counselor_attendance_ibfk_1` FOREIGN KEY (`attendance_id`) REFERENCES `Attendance_Record` (`attendance_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `counselor_attendance_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor_Attendance
-- ----------------------------

-- ----------------------------
-- Table structure for Counselor_Help
-- ----------------------------
DROP TABLE IF EXISTS `Counselor_Help`;
CREATE TABLE `Counselor_Help`  (
  `counselor_Id` int NOT NULL,
  `help_id` int NOT NULL,
  PRIMARY KEY (`counselor_Id`, `help_id`) USING BTREE,
  INDEX `help_id`(`help_id` ASC) USING BTREE,
  CONSTRAINT `counselor_help_ibfk_1` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `counselor_help_ibfk_2` FOREIGN KEY (`help_id`) REFERENCES `Help_Session` (`help_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor_Help
-- ----------------------------

-- ----------------------------
-- Table structure for Counselor_Queue_Record
-- ----------------------------
DROP TABLE IF EXISTS `Counselor_Queue_Record`;
CREATE TABLE `Counselor_Queue_Record`  (
  `queue_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  `assigned_time` datetime NOT NULL,
  PRIMARY KEY (`queue_id`, `counselor_id`) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `counselor_queue_record_ibfk_1` FOREIGN KEY (`queue_id`) REFERENCES `Queue` (`queue_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `counselor_queue_record_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor_Queue_Record
-- ----------------------------

-- ----------------------------
-- Table structure for Counselor_Schedule
-- ----------------------------
DROP TABLE IF EXISTS `Counselor_Schedule`;
CREATE TABLE `Counselor_Schedule`  (
  `schedule_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  PRIMARY KEY (`schedule_id`, `counselor_id`) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `counselor_schedule_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `counselor_schedule_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Counselor_Schedule
-- ----------------------------

-- ----------------------------
-- Table structure for duty_Schedule
-- ----------------------------
DROP TABLE IF EXISTS `duty_Schedule`;
CREATE TABLE `duty_Schedule`  (
  `duty_Id` int NOT NULL AUTO_INCREMENT,
  `staff_id` int NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `identity` enum('counselor','supervisor') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('scheduled','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'scheduled',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`duty_Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of duty_Schedule
-- ----------------------------

-- ----------------------------
-- Table structure for Export_Record
-- ----------------------------
DROP TABLE IF EXISTS `Export_Record`;
CREATE TABLE `Export_Record`  (
  `exported_id` int NOT NULL AUTO_INCREMENT,
  `export_role` enum('admin','counselor','supervisor') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `export_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `export_format` enum('csv','excel','pdf') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `export_time` datetime NOT NULL,
  `user_id` int NULL DEFAULT NULL,
  `counselor_id` int NULL DEFAULT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`exported_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `export_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `export_record_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Export_Record
-- ----------------------------

-- ----------------------------
-- Table structure for Help_Message
-- ----------------------------
DROP TABLE IF EXISTS `Help_Message`;
CREATE TABLE `Help_Message`  (
  `help_message_id` int NOT NULL AUTO_INCREMENT,
  `message_type` enum('text','image','file') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `message_sent_time` datetime NOT NULL,
  `message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `help_id` int NOT NULL,
  `counselor_id` int NULL DEFAULT NULL,
  `is_from_supervisor` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_message_id`) USING BTREE,
  INDEX `help_id`(`help_id` ASC) USING BTREE,
  INDEX `counselor_id`(`counselor_id` ASC) USING BTREE,
  CONSTRAINT `help_message_ibfk_1` FOREIGN KEY (`help_id`) REFERENCES `Help_Session` (`help_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `help_message_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Help_Message
-- ----------------------------

-- ----------------------------
-- Table structure for Help_Session
-- ----------------------------
DROP TABLE IF EXISTS `Help_Session`;
CREATE TABLE `Help_Session`  (
  `help_id` int NOT NULL AUTO_INCREMENT,
  `help_start_time` datetime NULL DEFAULT NULL,
  `help_end_time` datetime NULL DEFAULT NULL,
  `help_status` enum('requested','in_progress','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'requested',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Help_Session
-- ----------------------------

-- ----------------------------
-- Table structure for Leave_Application
-- ----------------------------
DROP TABLE IF EXISTS `Leave_Application`;
CREATE TABLE `Leave_Application`  (
  `leave_id` int NOT NULL AUTO_INCREMENT,
  `leave_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `leave_status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  `application_time` datetime NOT NULL,
  `supervisor_id` int NULL DEFAULT NULL,
  `counselor_Id` int NOT NULL,
  `schedule_Id` int NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`leave_id`) USING BTREE,
  INDEX `supervisor_id`(`supervisor_id` ASC) USING BTREE,
  INDEX `counselor_Id`(`counselor_Id` ASC) USING BTREE,
  INDEX `schedule_Id`(`schedule_Id` ASC) USING BTREE,
  CONSTRAINT `leave_application_ibfk_1` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `leave_application_ibfk_2` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `leave_application_ibfk_3` FOREIGN KEY (`schedule_Id`) REFERENCES `Schedule` (`schedule_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Leave_Application
-- ----------------------------

-- ----------------------------
-- Table structure for Queue
-- ----------------------------
DROP TABLE IF EXISTS `Queue`;
CREATE TABLE `Queue`  (
  `queue_id` int NOT NULL AUTO_INCREMENT,
  `queue_number` int NOT NULL,
  `join_queue_time` datetime NOT NULL,
  `queue_status` enum('waiting','in_service','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'waiting',
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`queue_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `queue_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Queue
-- ----------------------------

-- ----------------------------
-- Table structure for Schedule
-- ----------------------------
DROP TABLE IF EXISTS `Schedule`;
CREATE TABLE `Schedule`  (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `status` enum('available','booked','unavailable') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'available',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`schedule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Schedule
-- ----------------------------

-- ----------------------------
-- Table structure for Session_Message
-- ----------------------------
DROP TABLE IF EXISTS `Session_Message`;
CREATE TABLE `Session_Message`  (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `message_type` enum('text','image','file','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `message_sent_time` datetime NOT NULL,
  `message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `session_id` int NOT NULL,
  `user_id` int NULL DEFAULT NULL,
  `is_from_counselor` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `session_id`(`session_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `session_message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `Consultation_Session` (`session_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `session_message_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Session_Message
-- ----------------------------

-- ----------------------------
-- Table structure for Supervisor
-- ----------------------------
DROP TABLE IF EXISTS `Supervisor`;
CREATE TABLE `Supervisor`  (
  `supervisor_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `counselor_certificate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `is_supervisor` tinyint(1) NULL DEFAULT 1,
  `status` enum('active','inactive','on_leave') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active',
  `expertise_area` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `on_duty` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`supervisor_id`) USING BTREE,
  UNIQUE INDEX `phone_number`(`phone_number` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Supervisor
-- ----------------------------
INSERT INTO `Supervisor` VALUES (1, '王督导', '13800138003', 'hashed_password_3', 'supervisor2@example.com', NULL, 1, 'active', '家庭关系', 0, '2025-03-31 05:05:20', '2025-03-31 05:05:20');

-- ----------------------------
-- Table structure for Supervisor_Attendance
-- ----------------------------
DROP TABLE IF EXISTS `Supervisor_Attendance`;
CREATE TABLE `Supervisor_Attendance`  (
  `attendance_id` int NOT NULL,
  `supervisor_id` int NOT NULL,
  PRIMARY KEY (`attendance_id`, `supervisor_id`) USING BTREE,
  INDEX `supervisor_id`(`supervisor_id` ASC) USING BTREE,
  CONSTRAINT `supervisor_attendance_ibfk_1` FOREIGN KEY (`attendance_id`) REFERENCES `Attendance_Record` (`attendance_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `supervisor_attendance_ibfk_2` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Supervisor_Attendance
-- ----------------------------

-- ----------------------------
-- Table structure for Supervisor_Help
-- ----------------------------
DROP TABLE IF EXISTS `Supervisor_Help`;
CREATE TABLE `Supervisor_Help`  (
  `supervisor_id` int NOT NULL,
  `help_id` int NOT NULL,
  PRIMARY KEY (`supervisor_id`, `help_id`) USING BTREE,
  INDEX `help_id`(`help_id` ASC) USING BTREE,
  CONSTRAINT `supervisor_help_ibfk_1` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `supervisor_help_ibfk_2` FOREIGN KEY (`help_id`) REFERENCES `Help_Session` (`help_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Supervisor_Help
-- ----------------------------

-- ----------------------------
-- Table structure for Supervisor_Schedule
-- ----------------------------
DROP TABLE IF EXISTS `Supervisor_Schedule`;
CREATE TABLE `Supervisor_Schedule`  (
  `schedule_id` int NOT NULL,
  `supervisor_id` int NOT NULL,
  PRIMARY KEY (`schedule_id`, `supervisor_id`) USING BTREE,
  INDEX `supervisor_id`(`supervisor_id` ASC) USING BTREE,
  CONSTRAINT `supervisor_schedule_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `supervisor_schedule_ibfk_2` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Supervisor_Schedule
-- ----------------------------

-- ----------------------------
-- Table structure for User
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User`  (
  `user_Id` int NOT NULL AUTO_INCREMENT,
  `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gender` enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status_Info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `counselor_Id` int NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_Id`) USING BTREE,
  UNIQUE INDEX `open_id`(`open_id` ASC) USING BTREE,
  UNIQUE INDEX `phone_number`(`phone_number` ASC) USING BTREE,
  INDEX `counselor_Id`(`counselor_Id` ASC) USING BTREE,
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of User
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

