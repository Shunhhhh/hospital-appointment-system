package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.DoctorSchedule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班Mapper
 */
@Mapper
public interface ScheduleMapper {
    
    @Select("SELECT ds.*, d.doctorName, d.title AS doctorTitle, dept.departmentName, dept.departmentID " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor d ON ds.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE ds.doctorID = #{doctorId} AND ds.scheduleDate >= CURDATE() " +
            "ORDER BY ds.scheduleDate ASC, ds.timeSlot ASC")
    List<DoctorSchedule> selectByDoctor(Long doctorId);
    
    @Select("SELECT ds.*, d.doctorName, d.title AS doctorTitle, dept.departmentName, dept.departmentID " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor d ON ds.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE ds.scheduleDate = #{date} AND ds.scheduleStatus = 1 " +
            "ORDER BY ds.scheduleDate ASC, ds.timeSlot ASC")
    List<DoctorSchedule> selectByDate(LocalDate date);
    
    @Select("<script>" +
            "SELECT ds.*, d.doctorName, d.title AS doctorTitle, dept.departmentName, dept.departmentID " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor d ON ds.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE d.departmentID = #{departmentId} " +
            "<if test='startDate != null'> AND ds.scheduleDate &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND ds.scheduleDate &lt;= #{endDate} </if>" +
            "ORDER BY ds.scheduleDate ASC, ds.timeSlot ASC" +
            "</script>")
    List<DoctorSchedule> selectByDepartment(@Param("departmentId") Long departmentId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Select("<script>" +
            "SELECT ds.*, d.doctorName, d.title AS doctorTitle, dept.departmentName, dept.departmentID " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor d ON ds.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE ds.scheduleStatus = 1 AND ds.remainingSlots &gt; 0 " +
            "<if test='doctorId != null'> AND ds.doctorID = #{doctorId} </if>" +
            "<if test='departmentId != null'> AND d.departmentID = #{departmentId} </if>" +
            "<if test='startDate != null'> AND ds.scheduleDate &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND ds.scheduleDate &lt;= #{endDate} </if>" +
            "ORDER BY ds.scheduleDate ASC, ds.timeSlot ASC" +
            "</script>")
    List<DoctorSchedule> selectAvailable(@Param("doctorId") Long doctorId,
                                         @Param("departmentId") Long departmentId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    @Select("SELECT ds.*, d.doctorName, d.title AS doctorTitle, dept.departmentName, dept.departmentID " +
            "FROM doctor_schedule ds " +
            "LEFT JOIN doctor d ON ds.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE ds.scheduleID = #{id}")
    DoctorSchedule selectById(Long id);
    
    @Insert("INSERT INTO doctor_schedule (doctorID, scheduleDate, timeSlot, startTime, endTime, " +
            "totalSlots, remainingSlots, registeredSlots, price, registrationType, scheduleStatus) " +
            "VALUES (#{doctorID}, #{scheduleDate}, #{timeSlot}, #{startTime}, #{endTime}, " +
            "#{totalSlots}, #{remainingSlots}, #{registeredSlots}, #{price}, #{registrationType}, #{scheduleStatus})")
    int insert(DoctorSchedule schedule);
    
    @Update("UPDATE doctor_schedule SET scheduleDate = #{scheduleDate}, timeSlot = #{timeSlot}, " +
            "startTime = #{startTime}, endTime = #{endTime}, totalSlots = #{totalSlots}, " +
            "remainingSlots = #{remainingSlots}, registeredSlots = #{registeredSlots}, price = #{price}, " +
            "registrationType = #{registrationType}, scheduleStatus = #{scheduleStatus} WHERE scheduleID = #{scheduleID}")
    int update(DoctorSchedule schedule);
    
    @Delete("DELETE FROM doctor_schedule WHERE scheduleID = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE doctor_schedule SET scheduleStatus = #{status} WHERE scheduleID = #{id}")
    int updateStatus(Long id, Integer status);
    
    @Update("UPDATE doctor_schedule SET remainingSlots = remainingSlots - 1, registeredSlots = registeredSlots + 1 " +
            "WHERE scheduleID = #{id} AND remainingSlots &gt; 0")
    int decrementSlots(Long id);
    
    @Update("UPDATE doctor_schedule SET remainingSlots = remainingSlots + 1, registeredSlots = registeredSlots - 1 " +
            "WHERE scheduleID = #{id} AND registeredSlots &gt; 0")
    int incrementSlots(Long id);
}
