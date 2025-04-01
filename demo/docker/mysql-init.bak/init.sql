-- 创建数据库
CREATE DATABASE IF NOT EXISTS mydb;

-- 使用该数据库
USE mydb;

-- Counselor 心理咨询师表
CREATE TABLE IF NOT EXISTS Counselor (
    counselor_Id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    counselor_certificate TEXT,
    is_supervisor BOOLEAN DEFAULT FALSE,
    status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active',
    expertise_area VARCHAR(100),
    on_duty BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Supervisor 督导表
CREATE TABLE IF NOT EXISTS Supervisor (
    supervisor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    counselor_certificate TEXT,
    is_supervisor BOOLEAN DEFAULT TRUE,
    status ENUM('active', 'inactive', 'on_leave') DEFAULT 'active',
    expertise_area VARCHAR(100),
    on_duty BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User 用户表
CREATE TABLE IF NOT EXISTS User (
    user_Id INT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(100) UNIQUE,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    gender ENUM('male', 'female', 'other', 'unknown'),
    birthday DATE,
    occupation VARCHAR(50),
    phone_number VARCHAR(20) UNIQUE,
    status_Info VARCHAR(100),
    counselor_Id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (counselor_Id) REFERENCES Counselor(counselor_Id) ON DELETE SET NULL
);

-- Appointment 预约表
CREATE TABLE IF NOT EXISTS Appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_time DATETIME NOT NULL,
    appointment_status ENUM('pending', 'confirmed', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    user_Id INT NOT NULL,
    counselor_Id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_Id) REFERENCES User(user_Id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_Id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE
);

-- Consultation_Session 咨询会话表
CREATE TABLE IF NOT EXISTS Consultation_Session (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    session_start_time DATETIME,
    session_end_time DATETIME,
    session_status ENUM('scheduled', 'in_progress', 'completed', 'cancelled') DEFAULT 'scheduled',
    last_message_sent_time DATETIME,
    counselor_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE
);

-- Session_Message 会话消息表
CREATE TABLE IF NOT EXISTS Session_Message (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    message_type ENUM('text', 'image', 'file', 'system') NOT NULL,
    message_sent_time DATETIME NOT NULL,
    message_content TEXT NOT NULL,
    session_id INT NOT NULL,
    user_id INT,
    is_from_counselor BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(user_Id) ON DELETE SET NULL
);

-- Consultation_History 咨询历史表
CREATE TABLE IF NOT EXISTS Consultation_History (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    session_start_time DATETIME NOT NULL,
    session_end_time DATETIME NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    feedback_content TEXT,
    help_record TEXT,
    session_id INT NOT NULL,
    evaluation_id INT,
    user_id INT NOT NULL,
    counselor_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(user_Id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE
);

-- Consultation_Evaluation 咨询评估表
CREATE TABLE IF NOT EXISTS Consultation_Evaluation (
    evaluation_Id INT AUTO_INCREMENT PRIMARY KEY,
    user_visible_part TEXT,
    user_invisible_part TEXT,
    evaluation_time DATETIME NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    session_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES Consultation_Session(session_id) ON DELETE CASCADE
);

-- Queue 排队表
CREATE TABLE IF NOT EXISTS Queue (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    queue_number INT NOT NULL,
    join_queue_time DATETIME NOT NULL,
    queue_status ENUM('waiting', 'in_service', 'completed', 'cancelled') DEFAULT 'waiting',
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_Id) ON DELETE CASCADE
);

-- Counselor_Queue_Record 咨询师排队记录表
CREATE TABLE IF NOT EXISTS Counselor_Queue_Record (
    queue_id INT NOT NULL,
    counselor_id INT NOT NULL,
    assigned_time DATETIME NOT NULL,
    PRIMARY KEY (queue_id, counselor_id),
    FOREIGN KEY (queue_id) REFERENCES Queue(queue_id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE
);

-- Export_Record 导出记录表
CREATE TABLE IF NOT EXISTS Export_Record (
    exported_id INT AUTO_INCREMENT PRIMARY KEY,
    export_role ENUM('admin', 'counselor', 'supervisor') NOT NULL,
    export_type VARCHAR(50) NOT NULL,
    export_format ENUM('csv', 'excel', 'pdf') NOT NULL,
    export_time DATETIME NOT NULL,
    user_id INT,
    counselor_id INT,
    file_path VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_Id) ON DELETE SET NULL,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE SET NULL
);

-- Binding_Record 督导绑定记录表
CREATE TABLE IF NOT EXISTS Binding_Record (
    binding_id INT AUTO_INCREMENT PRIMARY KEY,
    binding_date DATE NOT NULL,
    binding_status ENUM('active', 'inactive', 'terminated') DEFAULT 'active',
    supervisor_id INT NOT NULL,
    counselor_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE,
    UNIQUE (supervisor_id, counselor_id)
);

-- Help_Session 督导帮助会话表
CREATE TABLE IF NOT EXISTS Help_Session (
    help_id INT AUTO_INCREMENT PRIMARY KEY,
    help_start_time DATETIME,
    help_end_time DATETIME,
    help_status ENUM('requested', 'in_progress', 'completed', 'cancelled') DEFAULT 'requested',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Supervisor_Help 督导帮助关联表
CREATE TABLE IF NOT EXISTS Supervisor_Help (
    supervisor_id INT NOT NULL,
    help_id INT NOT NULL,
    PRIMARY KEY (supervisor_id, help_id),
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id) ON DELETE CASCADE,
    FOREIGN KEY (help_id) REFERENCES Help_Session(help_id) ON DELETE CASCADE
);

-- Counselor_Help 咨询师帮助关联表
CREATE TABLE IF NOT EXISTS Counselor_Help (
    counselor_Id INT NOT NULL,
    help_id INT NOT NULL,
    PRIMARY KEY (counselor_Id, help_id),
    FOREIGN KEY (counselor_Id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE,
    FOREIGN KEY (help_id) REFERENCES Help_Session(help_id) ON DELETE CASCADE
);

-- Help_Message 帮助消息表
CREATE TABLE IF NOT EXISTS Help_Message (
    help_message_id INT AUTO_INCREMENT PRIMARY KEY,
    message_type ENUM('text', 'image', 'file') NOT NULL,
    message_sent_time DATETIME NOT NULL,
    message_content TEXT NOT NULL,
    help_id INT NOT NULL,
    counselor_id INT,
    is_from_supervisor BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (help_id) REFERENCES Help_Session(help_id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE SET NULL
);

-- Schedule 日程表
CREATE TABLE IF NOT EXISTS Schedule (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    time TIME NOT NULL,
    status ENUM('available', 'booked', 'unavailable') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Counselor_Schedule 咨询师日程关联表
CREATE TABLE IF NOT EXISTS Counselor_Schedule (
    schedule_id INT NOT NULL,
    counselor_id INT NOT NULL,
    PRIMARY KEY (schedule_id, counselor_id),
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE
);

-- Supervisor_Schedule 督导日程关联表
CREATE TABLE IF NOT EXISTS Supervisor_Schedule (
    schedule_id INT NOT NULL,
    supervisor_id INT NOT NULL,
    PRIMARY KEY (schedule_id, supervisor_id),
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id) ON DELETE CASCADE,
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id) ON DELETE CASCADE
);

-- duty_Schedule 值班表
CREATE TABLE IF NOT EXISTS duty_Schedule (
    duty_Id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    identity ENUM('counselor', 'supervisor') NOT NULL,
    status ENUM('scheduled', 'completed', 'cancelled') DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Attendance_Record 考勤记录表
CREATE TABLE IF NOT EXISTS Attendance_Record (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    clock_in_time DATETIME,
    clock_out_time DATETIME,
    attendance_status ENUM('present', 'late', 'absent', 'on_leave') DEFAULT 'absent',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Counselor_Attendance 咨询师考勤关联表
CREATE TABLE IF NOT EXISTS Counselor_Attendance (
    attendance_id INT NOT NULL,
    counselor_id INT NOT NULL,
    PRIMARY KEY (attendance_id, counselor_id),
    FOREIGN KEY (attendance_id) REFERENCES Attendance_Record(attendance_id) ON DELETE CASCADE,
    FOREIGN KEY (counselor_id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE
);

-- Supervisor_Attendance 督导考勤关联表
CREATE TABLE IF NOT EXISTS Supervisor_Attendance (
    attendance_id INT NOT NULL,
    supervisor_id INT NOT NULL,
    PRIMARY KEY (attendance_id, supervisor_id),
    FOREIGN KEY (attendance_id) REFERENCES Attendance_Record(attendance_id) ON DELETE CASCADE,
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id) ON DELETE CASCADE
);

-- Leave_Application 请假申请表
CREATE TABLE IF NOT EXISTS Leave_Application (
    leave_id INT AUTO_INCREMENT PRIMARY KEY,
    leave_reason TEXT NOT NULL,
    leave_status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    application_time DATETIME NOT NULL,
    supervisor_id INT,
    counselor_Id INT NOT NULL,
    schedule_Id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supervisor_id) REFERENCES Supervisor(supervisor_id) ON DELETE SET NULL,
    FOREIGN KEY (counselor_Id) REFERENCES Counselor(counselor_Id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_Id) REFERENCES Schedule(schedule_id) ON DELETE SET NULL
);

-- 插入一些初始数据,用于测试
INSERT INTO Counselor (name, phone_number, password, email, is_supervisor, status, expertise_area)
VALUES
('张心理咨询师', '13800138001', 'hashed_password_1', 'counselor1@example.com', FALSE, 'active', '焦虑症'),
('李督导', '13800138002', 'hashed_password_2', 'supervisor1@example.com', TRUE, 'active', '抑郁症');

INSERT INTO Supervisor (name, phone_number, password, email, is_supervisor, status, expertise_area)
VALUES
('王督导', '13800138003', 'hashed_password_3', 'supervisor2@example.com', TRUE, 'active', '家庭关系');


SELECT 'Database initialized successfully' AS status;