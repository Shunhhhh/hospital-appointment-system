package com.hospital.appointment.util;

import java.util.UUID;

/**
 * ID生成工具类
 */
public class UUIDGenerator {
    
    /**
     * 生成UUID
     */
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成带前缀的ID
     */
    public static String generateWithPrefix(String prefix) {
        return prefix + generate();
    }
    
    /**
     * 生成预约ID
     */
    public static String generateAppointmentID() {
        return "APT" + System.currentTimeMillis();
    }
    
    /**
     * 生成评价ID
     */
    public static String generateReviewID() {
        return "REV" + System.currentTimeMillis();
    }
    
    /**
     * 生成通知ID
     */
    public static String generateNotificationID() {
        return "NOT" + System.currentTimeMillis();
    }
}
