package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生实体类
 * 新增
 */
@Data
@TableName("doctor")
public class Doctor implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long doctorID;
    
    /** 医生姓名 */
    private String doctorName;
    
    /** 登录密码 */
    private String doctorPassword;
    
    /** 性别：1-男 2-女 */
    private Integer doctorGender;
    
    /** 手机号 */
    private String doctorPhone;
    
    /** 邮箱 */
    private String doctorEmail;
    
    /** 所属科室ID */
    private Long departmentID;
    
    /** 职称：主任医师 副主任医师 主治医师 住院医师 */
    private String title;
    
    /** 专长 */
    private String specialty;
    
    /** 医生简介 */
    private String doctorIntro;
    
    /** 医生照片URL */
    private String doctorPhoto;
    
    /** 挂号费 */
    private BigDecimal registrationFee;
    
    /** 医生状态：0-离职 1-在职 2-停诊 */
    private Integer doctorStatus;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 非数据库字段：科室名称 */
    @TableField(exist = false)
    private String departmentName;
}
