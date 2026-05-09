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

/**
 * 挂号记录实体类
 * 改造自预约记录(reservationrecord)
 */
@Data
@TableName("appointment")
public class Appointment implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private String appointmentID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 排班ID */
    private Long scheduleID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 科室ID */
    private Long departmentID;
    
    /** 预约就诊日期 */
    private LocalDate appointmentDate;
    
    /** 时段：1-上午 2-下午 3-夜诊 */
    private Integer timeSlot;
    
    /** 就诊序号 */
    private Integer appointmentNumber;
    
    /** 主诉/病情描述 */
    private String chiefComplaint;
    
    /** 挂号状态：0-待支付 1-已预约 2-已签到 3-就诊中 4-已完成 5-已取消 6-已退号 7-已爽约 */
    private Integer appointmentStatus;
    
    /** 支付状态：0-待支付 1-已支付 2-已退款 */
    private Integer paymentStatus;
    
    /** 支付金额 */
    private BigDecimal paymentAmount;
    
    /** 支付方式 */
    private String paymentMethod;
    
    /** 支付时间 */
    private LocalDateTime paymentTime;
    
    /** 取消原因 */
    private String cancelReason;
    
    /** 取消时间 */
    private LocalDateTime cancelTime;
    
    /** 是否已评价：0-否 1-是 */
    private Integer isReviewed;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 非数据库字段：医生姓名 */
    @TableField(exist = false)
    private String doctorName;
    
    /** 非数据库字段：科室名称 */
    @TableField(exist = false)
    private String departmentName;
    
    /** 非数据库字段：患者姓名 */
    @TableField(exist = false)
    private String patientName;
    
    /** 非数据库字段：出诊日期-时段格式 */
    @TableField(exist = false)
    private String scheduleTimeStr;
}
