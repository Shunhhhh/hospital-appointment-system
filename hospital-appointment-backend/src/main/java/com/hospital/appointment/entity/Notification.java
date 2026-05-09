package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息实体类
 * 保留原有结构
 */
@Data
@TableName("notification")
public class Notification implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private String notificationID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 管理员ID */
    private Integer adminID;
    
    /** 通知类型：1-预约成功 2-就诊提醒 3-候补成功 4-退号通知 5-医生回复 6-系统通知 */
    private Integer notificationType;
    
    /** 通知标题 */
    private String title;
    
    /** 通知内容 */
    private String notificationContent;
    
    /** 关联ID */
    private String relatedID;
    
    /** 状态：1-未读 2-已读 */
    private Integer notificationStatus;
    
    /** 发送时间 */
    private LocalDateTime sendTime;
    
    /** 阅读时间 */
    private LocalDateTime readTime;
    
    /** 过期时间 */
    private LocalDateTime expireTime;
}
