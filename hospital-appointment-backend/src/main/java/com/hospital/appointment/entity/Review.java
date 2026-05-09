package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 就医评价实体类
 * 改造自反馈表(feedback)
 */
@Data
@TableName("review")
public class Review implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private String reviewID;
    
    /** 患者ID */
    private Long patientID;
    
    /** 医生ID */
    private Long doctorID;
    
    /** 挂号记录ID */
    private String appointmentID;
    
    /** 科室ID */
    private Long departmentID;
    
    /** 就诊记录ID */
    private Long visitID;
    
    /** 总体评分 1-5 */
    private Integer overallRating;
    
    /** 服务态度评分 1-5 */
    private Integer attitudeRating;
    
    /** 医疗技术评分 1-5 */
    private Integer skillRating;
    
    /** 就医环境评分 1-5 */
    private Integer environmentRating;
    
    /** 评价内容 */
    private String reviewContent;
    
    /** 评价图片 */
    private String reviewImages;
    
    /** 医生回复 */
    private String replyContent;
    
    /** 回复时间 */
    private LocalDateTime replyTime;
    
    /** 是否匿名：0-否 1-是 */
    private Integer isAnonymous;
    
    /** 状态：1-待审核 2-已发布 3-已隐藏 */
    private Integer reviewStatus;
    
    /** 评价时间 */
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
