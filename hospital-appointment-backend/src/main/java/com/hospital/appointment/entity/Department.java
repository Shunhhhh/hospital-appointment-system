package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科室实体类
 * 改造自自习室(studyroom)
 */
@Data
@TableName("department")
public class Department implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long departmentID;
    
    /** 科室名称 */
    private String departmentName;
    
    /** 科室类型：1-内科 2-外科 3-儿科 4-妇科 5-产科 6-骨科 7-眼科 8-耳鼻喉科 9-口腔科 10-皮肤科 11-中医科 12-急诊科 */
    private Integer departmentType;
    
    /** 科室位置 */
    private String departmentLocation;
    
    /** 科室简介 */
    private String departmentDesc;
    
    /** 科室图标 */
    private String departmentIcon;
    
    /** 科室状态：0-停诊 1-正常 */
    private Integer departmentStatus;
    
    /** 显示顺序 */
    private Integer displayOrder;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
