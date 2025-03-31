-- MySQL dump 10.13  Distrib 8.3.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: user
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Appointment`
--

DROP TABLE IF EXISTS `Appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Appointment` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `appointment_time` datetime NOT NULL,
  `appointment_status` enum('pending','confirmed','completed','cancelled') DEFAULT 'pending',
  `notes` text,
  `user_Id` int NOT NULL,
  `counselor_Id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`appointment_id`),
  KEY `user_Id` (`user_Id`),
  KEY `counselor_Id` (`counselor_Id`),
  CONSTRAINT `Appointment_ibfk_1` FOREIGN KEY (`user_Id`) REFERENCES `User` (`user_Id`) ON DELETE CASCADE,
  CONSTRAINT `Appointment_ibfk_2` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Appointment`
--

LOCK TABLES `Appointment` WRITE;
/*!40000 ALTER TABLE `Appointment` DISABLE KEYS */;
/*!40000 ALTER TABLE `Appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Attendance_Record`
--

DROP TABLE IF EXISTS `Attendance_Record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Attendance_Record` (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `clock_in_time` datetime DEFAULT NULL,
  `clock_out_time` datetime DEFAULT NULL,
  `attendance_status` enum('present','late','absent','on_leave') DEFAULT 'absent',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`attendance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Attendance_Record`
--

LOCK TABLES `Attendance_Record` WRITE;
/*!40000 ALTER TABLE `Attendance_Record` DISABLE KEYS */;
/*!40000 ALTER TABLE `Attendance_Record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Binding_Record`
--

DROP TABLE IF EXISTS `Binding_Record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Binding_Record` (
  `binding_id` int NOT NULL AUTO_INCREMENT,
  `binding_date` date NOT NULL,
  `binding_status` enum('active','inactive','terminated') DEFAULT 'active',
  `supervisor_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`binding_id`),
  UNIQUE KEY `supervisor_id` (`supervisor_id`,`counselor_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Binding_Record_ibfk_1` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE,
  CONSTRAINT `Binding_Record_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Binding_Record`
--

LOCK TABLES `Binding_Record` WRITE;
/*!40000 ALTER TABLE `Binding_Record` DISABLE KEYS */;
/*!40000 ALTER TABLE `Binding_Record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Consultation_Evaluation`
--

DROP TABLE IF EXISTS `Consultation_Evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Consultation_Evaluation` (
  `evaluation_Id` int NOT NULL AUTO_INCREMENT,
  `user_visible_part` text,
  `user_invisible_part` text,
  `evaluation_time` datetime NOT NULL,
  `rating` int DEFAULT NULL,
  `session_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`evaluation_Id`),
  KEY `session_id` (`session_id`),
  CONSTRAINT `Consultation_Evaluation_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `Consultation_Session` (`session_id`) ON DELETE CASCADE,
  CONSTRAINT `Consultation_Evaluation_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Consultation_Evaluation`
--

LOCK TABLES `Consultation_Evaluation` WRITE;
/*!40000 ALTER TABLE `Consultation_Evaluation` DISABLE KEYS */;
/*!40000 ALTER TABLE `Consultation_Evaluation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Consultation_History`
--

DROP TABLE IF EXISTS `Consultation_History`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Consultation_History` (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `session_start_time` datetime NOT NULL,
  `session_end_time` datetime NOT NULL,
  `rating` int DEFAULT NULL,
  `feedback_content` text,
  `help_record` text,
  `session_id` int NOT NULL,
  `evaluation_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`history_id`),
  KEY `session_id` (`session_id`),
  KEY `user_id` (`user_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Consultation_History_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `Consultation_Session` (`session_id`) ON DELETE CASCADE,
  CONSTRAINT `Consultation_History_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE CASCADE,
  CONSTRAINT `Consultation_History_ibfk_3` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE,
  CONSTRAINT `Consultation_History_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Consultation_History`
--

LOCK TABLES `Consultation_History` WRITE;
/*!40000 ALTER TABLE `Consultation_History` DISABLE KEYS */;
/*!40000 ALTER TABLE `Consultation_History` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Consultation_Session`
--

DROP TABLE IF EXISTS `Consultation_Session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Consultation_Session` (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `session_start_time` datetime DEFAULT NULL,
  `session_end_time` datetime DEFAULT NULL,
  `session_status` enum('scheduled','in_progress','completed','cancelled') DEFAULT 'scheduled',
  `last_message_sent_time` datetime DEFAULT NULL,
  `counselor_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`session_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Consultation_Session_ibfk_1` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Consultation_Session`
--

LOCK TABLES `Consultation_Session` WRITE;
/*!40000 ALTER TABLE `Consultation_Session` DISABLE KEYS */;
/*!40000 ALTER TABLE `Consultation_Session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Counselor`
--

DROP TABLE IF EXISTS `Counselor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Counselor` (
  `counselor_Id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `counselor_certificate` text,
  `is_supervisor` tinyint(1) DEFAULT '0',
  `status` enum('active','inactive','on_leave') DEFAULT 'active',
  `expertise_area` varchar(100) DEFAULT NULL,
  `on_duty` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`counselor_Id`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Counselor`
--

LOCK TABLES `Counselor` WRITE;
/*!40000 ALTER TABLE `Counselor` DISABLE KEYS */;
INSERT INTO `Counselor` VALUES (1,'张心理咨询师','13800138001','hashed_password_1','counselor1@example.com',NULL,0,'active','焦虑症',0,'2025-03-31 04:57:55','2025-03-31 04:57:55'),(2,'李督导','13800138002','hashed_password_2','supervisor1@example.com',NULL,1,'active','抑郁症',0,'2025-03-31 04:57:55','2025-03-31 04:57:55');
/*!40000 ALTER TABLE `Counselor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Counselor_Attendance`
--

DROP TABLE IF EXISTS `Counselor_Attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Counselor_Attendance` (
  `attendance_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  PRIMARY KEY (`attendance_id`,`counselor_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Counselor_Attendance_ibfk_1` FOREIGN KEY (`attendance_id`) REFERENCES `Attendance_Record` (`attendance_id`) ON DELETE CASCADE,
  CONSTRAINT `Counselor_Attendance_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Counselor_Attendance`
--

LOCK TABLES `Counselor_Attendance` WRITE;
/*!40000 ALTER TABLE `Counselor_Attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `Counselor_Attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Counselor_Help`
--

DROP TABLE IF EXISTS `Counselor_Help`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Counselor_Help` (
  `counselor_Id` int NOT NULL,
  `help_id` int NOT NULL,
  PRIMARY KEY (`counselor_Id`,`help_id`),
  KEY `help_id` (`help_id`),
  CONSTRAINT `Counselor_Help_ibfk_1` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE,
  CONSTRAINT `Counselor_Help_ibfk_2` FOREIGN KEY (`help_id`) REFERENCES `Help_Session` (`help_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Counselor_Help`
--

LOCK TABLES `Counselor_Help` WRITE;
/*!40000 ALTER TABLE `Counselor_Help` DISABLE KEYS */;
/*!40000 ALTER TABLE `Counselor_Help` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Counselor_Queue_Record`
--

DROP TABLE IF EXISTS `Counselor_Queue_Record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Counselor_Queue_Record` (
  `queue_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  `assigned_time` datetime NOT NULL,
  PRIMARY KEY (`queue_id`,`counselor_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Counselor_Queue_Record_ibfk_1` FOREIGN KEY (`queue_id`) REFERENCES `Queue` (`queue_id`) ON DELETE CASCADE,
  CONSTRAINT `Counselor_Queue_Record_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Counselor_Queue_Record`
--

LOCK TABLES `Counselor_Queue_Record` WRITE;
/*!40000 ALTER TABLE `Counselor_Queue_Record` DISABLE KEYS */;
/*!40000 ALTER TABLE `Counselor_Queue_Record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Counselor_Schedule`
--

DROP TABLE IF EXISTS `Counselor_Schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Counselor_Schedule` (
  `schedule_id` int NOT NULL,
  `counselor_id` int NOT NULL,
  PRIMARY KEY (`schedule_id`,`counselor_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Counselor_Schedule_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`) ON DELETE CASCADE,
  CONSTRAINT `Counselor_Schedule_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Counselor_Schedule`
--

LOCK TABLES `Counselor_Schedule` WRITE;
/*!40000 ALTER TABLE `Counselor_Schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `Counselor_Schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Export_Record`
--

DROP TABLE IF EXISTS `Export_Record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Export_Record` (
  `exported_id` int NOT NULL AUTO_INCREMENT,
  `export_role` enum('admin','counselor','supervisor') NOT NULL,
  `export_type` varchar(50) NOT NULL,
  `export_format` enum('csv','excel','pdf') NOT NULL,
  `export_time` datetime NOT NULL,
  `user_id` int DEFAULT NULL,
  `counselor_id` int DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`exported_id`),
  KEY `user_id` (`user_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Export_Record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE SET NULL,
  CONSTRAINT `Export_Record_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Export_Record`
--

LOCK TABLES `Export_Record` WRITE;
/*!40000 ALTER TABLE `Export_Record` DISABLE KEYS */;
/*!40000 ALTER TABLE `Export_Record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Help_Message`
--

DROP TABLE IF EXISTS `Help_Message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Help_Message` (
  `help_message_id` int NOT NULL AUTO_INCREMENT,
  `message_type` enum('text','image','file') NOT NULL,
  `message_sent_time` datetime NOT NULL,
  `message_content` text NOT NULL,
  `help_id` int NOT NULL,
  `counselor_id` int DEFAULT NULL,
  `is_from_supervisor` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_message_id`),
  KEY `help_id` (`help_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `Help_Message_ibfk_1` FOREIGN KEY (`help_id`) REFERENCES `Help_Session` (`help_id`) ON DELETE CASCADE,
  CONSTRAINT `Help_Message_ibfk_2` FOREIGN KEY (`counselor_id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Help_Message`
--

LOCK TABLES `Help_Message` WRITE;
/*!40000 ALTER TABLE `Help_Message` DISABLE KEYS */;
/*!40000 ALTER TABLE `Help_Message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Help_Session`
--

DROP TABLE IF EXISTS `Help_Session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Help_Session` (
  `help_id` int NOT NULL AUTO_INCREMENT,
  `help_start_time` datetime DEFAULT NULL,
  `help_end_time` datetime DEFAULT NULL,
  `help_status` enum('requested','in_progress','completed','cancelled') DEFAULT 'requested',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`help_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Help_Session`
--

LOCK TABLES `Help_Session` WRITE;
/*!40000 ALTER TABLE `Help_Session` DISABLE KEYS */;
/*!40000 ALTER TABLE `Help_Session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Leave_Application`
--

DROP TABLE IF EXISTS `Leave_Application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Leave_Application` (
  `leave_id` int NOT NULL AUTO_INCREMENT,
  `leave_reason` text NOT NULL,
  `leave_status` enum('pending','approved','rejected') DEFAULT 'pending',
  `application_time` datetime NOT NULL,
  `supervisor_id` int DEFAULT NULL,
  `counselor_Id` int NOT NULL,
  `schedule_Id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`leave_id`),
  KEY `supervisor_id` (`supervisor_id`),
  KEY `counselor_Id` (`counselor_Id`),
  KEY `schedule_Id` (`schedule_Id`),
  CONSTRAINT `Leave_Application_ibfk_1` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE SET NULL,
  CONSTRAINT `Leave_Application_ibfk_2` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE CASCADE,
  CONSTRAINT `Leave_Application_ibfk_3` FOREIGN KEY (`schedule_Id`) REFERENCES `Schedule` (`schedule_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Leave_Application`
--

LOCK TABLES `Leave_Application` WRITE;
/*!40000 ALTER TABLE `Leave_Application` DISABLE KEYS */;
/*!40000 ALTER TABLE `Leave_Application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Queue`
--

DROP TABLE IF EXISTS `Queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Queue` (
  `queue_id` int NOT NULL AUTO_INCREMENT,
  `queue_number` int NOT NULL,
  `join_queue_time` datetime NOT NULL,
  `queue_status` enum('waiting','in_service','completed','cancelled') DEFAULT 'waiting',
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`queue_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `Queue_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Queue`
--

LOCK TABLES `Queue` WRITE;
/*!40000 ALTER TABLE `Queue` DISABLE KEYS */;
/*!40000 ALTER TABLE `Queue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Schedule`
--

DROP TABLE IF EXISTS `Schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Schedule` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `status` enum('available','booked','unavailable') DEFAULT 'available',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Schedule`
--

LOCK TABLES `Schedule` WRITE;
/*!40000 ALTER TABLE `Schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `Schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Session_Message`
--

DROP TABLE IF EXISTS `Session_Message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Session_Message` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `message_type` enum('text','image','file','system') NOT NULL,
  `message_sent_time` datetime NOT NULL,
  `message_content` text NOT NULL,
  `session_id` int NOT NULL,
  `user_id` int DEFAULT NULL,
  `is_from_counselor` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  KEY `session_id` (`session_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `Session_Message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `Consultation_Session` (`session_id`) ON DELETE CASCADE,
  CONSTRAINT `Session_Message_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_Id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Session_Message`
--

LOCK TABLES `Session_Message` WRITE;
/*!40000 ALTER TABLE `Session_Message` DISABLE KEYS */;
/*!40000 ALTER TABLE `Session_Message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Supervisor`
--

DROP TABLE IF EXISTS `Supervisor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supervisor` (
  `supervisor_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `counselor_certificate` text,
  `is_supervisor` tinyint(1) DEFAULT '1',
  `status` enum('active','inactive','on_leave') DEFAULT 'active',
  `expertise_area` varchar(100) DEFAULT NULL,
  `on_duty` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`supervisor_id`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Supervisor`
--

LOCK TABLES `Supervisor` WRITE;
/*!40000 ALTER TABLE `Supervisor` DISABLE KEYS */;
INSERT INTO `Supervisor` VALUES (1,'王督导','13800138003','hashed_password_3','supervisor2@example.com',NULL,1,'active','家庭关系',0,'2025-03-31 04:57:55','2025-03-31 04:57:55');
/*!40000 ALTER TABLE `Supervisor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Supervisor_Attendance`
--

DROP TABLE IF EXISTS `Supervisor_Attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supervisor_Attendance` (
  `attendance_id` int NOT NULL,
  `supervisor_id` int NOT NULL,
  PRIMARY KEY (`attendance_id`,`supervisor_id`),
  KEY `supervisor_id` (`supervisor_id`),
  CONSTRAINT `Supervisor_Attendance_ibfk_1` FOREIGN KEY (`attendance_id`) REFERENCES `Attendance_Record` (`attendance_id`) ON DELETE CASCADE,
  CONSTRAINT `Supervisor_Attendance_ibfk_2` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Supervisor_Attendance`
--

LOCK TABLES `Supervisor_Attendance` WRITE;
/*!40000 ALTER TABLE `Supervisor_Attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `Supervisor_Attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Supervisor_Help`
--

DROP TABLE IF EXISTS `Supervisor_Help`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supervisor_Help` (
  `supervisor_id` int NOT NULL,
  `help_id` int NOT NULL,
  PRIMARY KEY (`supervisor_id`,`help_id`),
  KEY `help_id` (`help_id`),
  CONSTRAINT `Supervisor_Help_ibfk_1` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE,
  CONSTRAINT `Supervisor_Help_ibfk_2` FOREIGN KEY (`help_id`) REFERENCES `Help_Session` (`help_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Supervisor_Help`
--

LOCK TABLES `Supervisor_Help` WRITE;
/*!40000 ALTER TABLE `Supervisor_Help` DISABLE KEYS */;
/*!40000 ALTER TABLE `Supervisor_Help` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Supervisor_Schedule`
--

DROP TABLE IF EXISTS `Supervisor_Schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Supervisor_Schedule` (
  `schedule_id` int NOT NULL,
  `supervisor_id` int NOT NULL,
  PRIMARY KEY (`schedule_id`,`supervisor_id`),
  KEY `supervisor_id` (`supervisor_id`),
  CONSTRAINT `Supervisor_Schedule_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`) ON DELETE CASCADE,
  CONSTRAINT `Supervisor_Schedule_ibfk_2` FOREIGN KEY (`supervisor_id`) REFERENCES `Supervisor` (`supervisor_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Supervisor_Schedule`
--

LOCK TABLES `Supervisor_Schedule` WRITE;
/*!40000 ALTER TABLE `Supervisor_Schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `Supervisor_Schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `user_Id` int NOT NULL AUTO_INCREMENT,
  `open_id` varchar(100) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `gender` enum('male','female','other','unknown') DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `occupation` varchar(50) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `status_Info` varchar(100) DEFAULT NULL,
  `counselor_Id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_Id`),
  UNIQUE KEY `open_id` (`open_id`),
  UNIQUE KEY `phone_number` (`phone_number`),
  KEY `counselor_Id` (`counselor_Id`),
  CONSTRAINT `User_ibfk_1` FOREIGN KEY (`counselor_Id`) REFERENCES `Counselor` (`counselor_Id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `duty_Schedule`
--

DROP TABLE IF EXISTS `duty_Schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `duty_Schedule` (
  `duty_Id` int NOT NULL AUTO_INCREMENT,
  `staff_id` int NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `identity` enum('counselor','supervisor') NOT NULL,
  `status` enum('scheduled','completed','cancelled') DEFAULT 'scheduled',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`duty_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duty_Schedule`
--

LOCK TABLES `duty_Schedule` WRITE;
/*!40000 ALTER TABLE `duty_Schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `duty_Schedule` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-31 13:09:30
