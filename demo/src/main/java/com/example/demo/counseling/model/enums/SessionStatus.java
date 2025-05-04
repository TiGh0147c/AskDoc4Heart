package com.example.demo.counseling.model.enums;

public enum SessionStatus {
    in_progress, completed;
    
    // 静态常量，保持与原来代码兼容
    public static final SessionStatus IN_PROGRESS = in_progress;
    public static final SessionStatus COMPLETED = completed;
    
    // 添加一个方法用于获取大写格式的名称（如果需要）
    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}