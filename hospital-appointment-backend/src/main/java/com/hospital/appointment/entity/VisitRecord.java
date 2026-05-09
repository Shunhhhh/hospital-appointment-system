package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 就诊记录实体类
 * 改造自考勤记录(attendancerecord)
 */
@Data
@TableName("visit_record")
public class VisitRecord implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long visitID;
    
    /** 挂号记录ID */
    private String appointmentID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 科室ID */
    private Long departmentID;
    
    /** 签到时间 */
    private LocalDateTime checkInTime;
    
    /** 开始就诊时间 */
    private LocalDateTime seeDoctorTime;
    
    /** 就诊完成时间 */
    private LocalDateTime finishTime;
    
    /** 排队号码 */
    private Integer waitingNumber;
    
    /** 就诊状态：1-已签到待就诊 2-就诊中 3-已完成 4-取消 */
    private Integer visitStatus;
    
    /** 主诉 */
    private String chiefComplaint;
    
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
}
