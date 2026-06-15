package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 门诊病历实体类
 * 新增
 */
@Data
@TableName("medical_record")
public class MedicalRecord implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long recordID;
    
    /** 就诊记录ID */
    private Long visitID;
    
    /** 挂号记录ID */
    private String appointmentID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 主诉 */
    private String chiefComplaint;
    
    /** 现病史 */
    private String presentIllness;
    
    /** 既往史 */
    private String pastHistory;
    
    /** 过敏史 */
    private String allergyHistory;
    
    /** 体格检查 */
    private String physicalExamination;
    
    /** 辅助检查 */
    private String auxiliaryExamination;
    
    /** 初步诊断 */
    private String preliminaryDiagnosis;
    
    /** 最终诊断 */
    private String finalDiagnosis;
    
    /** 治疗方案 */
    private String treatmentPlan;
    
    /** 医嘱 */
    private String medicalAdvice;
    
    /** 备注 */
    private String remarks;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 非数据库字段：医生姓名 */
    @TableField(exist = false)
    private String doctorName;
    
    /** 非数据库字段：患者姓名 */
    @TableField(exist = false)
    private String patientName;

    /** 非数据库字段：科室名称 */
    @TableField(exist = false)
    private String departmentName;
}
