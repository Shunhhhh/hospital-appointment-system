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
 * 处方实体类
 * 新增
 */
@Data
@TableName("prescription")
public class Prescription implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long prescriptionID;
    
    /** 病历ID */
    private Long recordID;
    
    /** 就诊记录ID */
    private Long visitID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 开方医生ID */
    private Long doctorID;
    
    /** 药品名称 */
    private String medicineName;
    
    /** 药品规格 */
    private String medicineSpec;
    
    /** 单次剂量 */
    private String dosage;
    
    /** 用法 */
    private String usage;
    
    /** 频率 */
    private String frequency;
    
    /** 疗程 */
    private String course;
    
    /** 数量 */
    private String quantity;
    
    /** 单位 */
    private String unit;
    
    /** 单价 */
    private BigDecimal price;
    
    /** 总价 */
    private BigDecimal totalPrice;
    
    /** 备注 */
    private String remarks;
    
    /** 处方类型：1-西药 2-中成药 3-中药 */
    private Integer prescriptionType;
    
    /** 状态：1-待缴费 2-已缴费 3-已发药 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
