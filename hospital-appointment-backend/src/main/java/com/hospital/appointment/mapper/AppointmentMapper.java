package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Appointment;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 挂号Mapper
 */
@Mapper
public interface AppointmentMapper {
    
    @Select("<script>" +
                        "SELECT a.*, ds.startTime AS scheduleStartTime, ds.endTime AS scheduleEndTime, d.doctorName, dept.departmentName, p.patientName " +
            "FROM appointment a " +
                        "LEFT JOIN doctor_schedule ds ON a.scheduleID = ds.scheduleID " +
            "LEFT JOIN doctor d ON a.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON a.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON a.patientID = p.patientID " +
            "WHERE a.patientID = #{patientId} " +
            "<if test='status != null'> AND a.appointmentStatus = #{status} </if>" +
            "ORDER BY a.appointmentDate DESC, a.timeSlot DESC" +
            "</script>")
    List<Appointment> selectByPatient(@Param("patientId") Long patientId, @Param("status") Integer status);
    
    @Select("<script>" +
            "SELECT a.*, ds.startTime AS scheduleStartTime, ds.endTime AS scheduleEndTime, d.doctorName, dept.departmentName, p.patientName " +
            "FROM appointment a " +
            "LEFT JOIN doctor_schedule ds ON a.scheduleID = ds.scheduleID " +
            "LEFT JOIN doctor d ON a.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON a.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON a.patientID = p.patientID " +
            "WHERE a.doctorID = #{doctorId} " +
            "<if test='status != null'> AND a.appointmentStatus = #{status} </if>" +
            "ORDER BY a.appointmentDate DESC, a.appointmentNumber ASC" +
            "</script>")
    List<Appointment> selectByDoctor(@Param("doctorId") Long doctorId, @Param("status") Integer status);
    
    @Select("SELECT a.*, ds.startTime AS scheduleStartTime, ds.endTime AS scheduleEndTime, d.doctorName, dept.departmentName, p.patientName " +
            "FROM appointment a " +
            "LEFT JOIN doctor_schedule ds ON a.scheduleID = ds.scheduleID " +
            "LEFT JOIN doctor d ON a.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON a.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON a.patientID = p.patientID " +
            "WHERE a.appointmentID = #{id}")
    Appointment selectById(String id);
    
    @Select("SELECT a.*, ds.startTime AS scheduleStartTime, ds.endTime AS scheduleEndTime, d.doctorName, dept.departmentName, p.patientName " +
            "FROM appointment a " +
            "LEFT JOIN doctor_schedule ds ON a.scheduleID = ds.scheduleID " +
            "LEFT JOIN doctor d ON a.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON a.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON a.patientID = p.patientID " +
            "WHERE a.doctorID = #{doctorId} AND a.appointmentDate = #{date} " +
            "ORDER BY a.appointmentNumber ASC")
    List<Appointment> selectTodayByDoctor(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Select("SELECT a.*, ds.startTime AS scheduleStartTime, ds.endTime AS scheduleEndTime, d.doctorName, dept.departmentName, p.patientName " +
            "FROM appointment a " +
            "LEFT JOIN doctor_schedule ds ON a.scheduleID = ds.scheduleID " +
            "LEFT JOIN doctor d ON a.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON a.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON a.patientID = p.patientID " +
            "WHERE a.patientID = #{patientId} AND a.appointmentDate = #{appointmentDate} AND a.timeSlot = #{timeSlot} " +
            "AND a.appointmentStatus NOT IN (5, 6, 7, 8) " +
            "LIMIT 1")
    Appointment selectByPatientAndSlot(@Param("patientId") Long patientId,
                                       @Param("appointmentDate") LocalDate appointmentDate,
                                       @Param("timeSlot") Integer timeSlot);

    @Select("SELECT a.*, ds.startTime AS scheduleStartTime, ds.endTime AS scheduleEndTime, d.doctorName, dept.departmentName, p.patientName " +
            "FROM appointment a " +
            "LEFT JOIN doctor_schedule ds ON a.scheduleID = ds.scheduleID " +
            "LEFT JOIN doctor d ON a.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON a.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON a.patientID = p.patientID " +
            "WHERE a.appointmentStatus = 1 AND TIMESTAMP(a.appointmentDate, ds.startTime) <= DATE_SUB(NOW(), INTERVAL 5 MINUTE)")
    List<Appointment> selectTimeoutAppointments();
    
    @Select("SELECT COUNT(*) FROM appointment WHERE doctorID = #{doctorId} AND appointmentDate = #{date}")
    long countByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    @Insert("INSERT INTO appointment (appointmentID, patientID, scheduleID, doctorID, departmentID, " +
            "appointmentDate, timeSlot, appointmentNumber, chiefComplaint, appointmentStatus, paymentStatus, " +
            "paymentAmount, paymentMethod, paymentTime, isReviewed, createTime) " +
            "VALUES (#{appointmentID}, #{patientID}, #{scheduleID}, #{doctorID}, #{departmentID}, " +
            "#{appointmentDate}, #{timeSlot}, #{appointmentNumber}, #{chiefComplaint}, #{appointmentStatus}, #{paymentStatus}, " +
            "#{paymentAmount}, #{paymentMethod}, #{paymentTime}, #{isReviewed}, #{createTime})")
    int insert(Appointment appointment);
    
    @Update("UPDATE appointment SET appointmentStatus = #{appointmentStatus}, cancelReason = #{cancelReason}, " +
            "cancelTime = #{cancelTime}, paymentStatus = #{paymentStatus} WHERE appointmentID = #{appointmentID}")
    int update(Appointment appointment);

    @Update("UPDATE appointment SET appointmentStatus = #{status} WHERE appointmentID = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") Integer status);
    
    @Update("UPDATE appointment SET isReviewed = #{isReviewed} WHERE appointmentID = #{appointmentID}")
    int updateIsReviewed(@Param("appointmentID") String appointmentID, @Param("isReviewed") Integer isReviewed);
    
    @Delete("DELETE FROM appointment WHERE appointmentID = #{id}")
    int deleteById(String id);
}
