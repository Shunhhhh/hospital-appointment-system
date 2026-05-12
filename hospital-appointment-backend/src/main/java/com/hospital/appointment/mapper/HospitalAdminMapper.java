package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员 Mapper（使用 @Mapper 注解而非 @Repository 避免与旧 AdminMapper 冲突）
 */
@Mapper
public interface HospitalAdminMapper {

    @Select("SELECT * FROM admin WHERE adminID = #{adminID}")
    Admin selectById(@Param("adminID") Integer adminID);
}
