package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("check_report")
public class CheckReport implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long reportID;

    private Long patientID;
    private Long doctorID;
    private Long departmentID;
    private String appointmentID;
    private String reportName;
    private String reportType;
    private String reportContent;
    private String doctorAdvice;
    private Integer reportStatus;
    private LocalDate checkDate;
    private LocalDateTime createTime;

    @TableField(exist = false) private String doctorName;
    @TableField(exist = false) private String departmentName;
}
