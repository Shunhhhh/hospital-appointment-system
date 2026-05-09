package com.hospital.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 */
@Data
@TableName("admin")
public class Admin implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Integer adminID;
    
    private String adminPassword;
    
    private String adminPosition;
    
    private Integer adminPermission;
    
    private String adminPhoneNumber;
    
    private String adminName;
}
