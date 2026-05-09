package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者实体类
 * 改造自学生用户(studentuser)
 */
@Data
@TableName("patient")
public class Patient implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long patientID;
    
    /** 患者姓名 */
    private String patientName;
    
    /** 登录密码 */
    private String patientPassword;
    
    /** 身份证号 */
    private String idCard;
    
    /** 性别：1-男 2-女 */
    private Integer patientGender;
    
    /** 出生日期 */
    private LocalDate patientBirthday;
    
    /** 手机号 */
    private String patientPhone;
    
    /** 邮箱 */
    private String patientEmail;
    
    /** 住址 */
    private String patientAddress;
    
    /** 就诊卡号 */
    private String medicalCardNo;
    
    /** 医保类型：1-城镇职工 2-城乡居民 3-自费 */
    private Integer insuranceType;
    
    /** 医保卡号 */
    private String insuranceNo;
    
    /** 过敏史 */
    private String allergyHistory;
    
    /** 既往病史 */
    private String medicalHistory;
    
    /** 紧急联系人 */
    private String emergencyContact;
    
    /** 紧急联系电话 */
    private String emergencyPhone;
    
    /** 信用积分 */
    private Integer creditScore;
    
    /** 爽约次数 */
    private Integer noshowCount;
    
    /** 是否黑名单：0-否 1-是 */
    private Integer isBlacklist;
    
    /** 账号状态：0-禁用 1-正常 */
    private Integer patientStatus;
    
    /** 注册时间 */
    private LocalDateTime createTime;
}
