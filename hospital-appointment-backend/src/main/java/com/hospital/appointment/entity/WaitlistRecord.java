package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 候补记录实体类
 * 改造自候补记录表(waitlistrecord)
 */
@Data
@TableName("waitlist_record")
public class WaitlistRecord implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private String waitlistID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 科室ID */
    private Long departmentID;
    
    /** 候补日期 */
    private LocalDate waitlistDate;
    
    /** 时段：1-上午 2-下午 3-夜诊 */
    private Integer timeSlot;
    
    /** 病情描述 */
    private String chiefComplaint;
    
    /** 候补状态：0-候补中 1-已获得号源 2-已过期 3-已取消 */
    private Integer waitlistStatus;
    
    /** 通知状态：0-未通知 1-已通知 2-已过期 */
    private Integer notifyStatus;
    
    /** 通知时间 */
    private LocalDateTime notifyTime;
    
    /** 候补有效截止时间 */
    private LocalDateTime validUntil;
    
    /** 优先级 */
    private Integer priority;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 非数据库字段：医生姓名 */
    @TableField(exist = false)
    private String doctorName;
    
    /** 非数据库字段：科室名称 */
    @TableField(exist = false)
    private String departmentName;
}
