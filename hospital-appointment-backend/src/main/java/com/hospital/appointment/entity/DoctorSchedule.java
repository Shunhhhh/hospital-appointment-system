package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 医生排班实体类
 * 改造自座位表(seat)
 */
@Data
@TableName("doctor_schedule")
public class DoctorSchedule implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long scheduleID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 出诊日期 */
    private LocalDate scheduleDate;
    
    /** 时段：1-上午 2-下午 3-夜诊 */
    private Integer timeSlot;
    
    /** 开始时间 */
    private LocalTime startTime;
    
    /** 结束时间 */
    private LocalTime endTime;
    
    /** 总号源数 */
    private Integer totalSlots;
    
    /** 剩余号源 */
    private Integer remainingSlots;
    
    /** 已预约数 */
    private Integer registeredSlots;
    
    /** 挂号费 */
    private BigDecimal price;
    
    /** 号源类型：1-普通门诊 2-专家门诊 3-特需门诊 */
    private Integer registrationType;
    
    /** 排班状态：0-已停诊 1-可预约 2-已约满 */
    private Integer scheduleStatus;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 非数据库字段：医生姓名 */
    @TableField(exist = false)
    private String doctorName;
    
    /** 非数据库字段：科室名称 */
    @TableField(exist = false)
    private String departmentName;
    
    /** 非数据库字段：科室ID */
    @TableField(exist = false)
    private Long departmentID;
    
    /** 非数据库字段：医生职称 */
    @TableField(exist = false)
    private String doctorTitle;
}
