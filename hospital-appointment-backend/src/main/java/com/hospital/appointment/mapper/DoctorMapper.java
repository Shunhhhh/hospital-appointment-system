package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Doctor;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 医生Mapper
 */
@Mapper
public interface DoctorMapper {
    
    @Select("SELECT d.*, dept.departmentName FROM doctor d " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE d.doctorStatus = 1 ORDER BY d.doctorID ASC")
    List<Doctor> selectAll();
    
    @Select("SELECT d.*, dept.departmentName FROM doctor d " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE d.doctorID = #{id}")
    Doctor selectById(Long id);
    
    @Select("SELECT d.*, dept.departmentName FROM doctor d " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE d.departmentID = #{departmentId} AND d.doctorStatus = 1")
    List<Doctor> selectByDepartment(Long departmentId);
    
    @Select("SELECT d.*, dept.departmentName FROM doctor d " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE d.title = #{title} AND d.doctorStatus = 1")
    List<Doctor> selectByTitle(String title);
    
    @Select("SELECT d.*, dept.departmentName FROM doctor d " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "WHERE (d.doctorName LIKE CONCAT('%', #{keyword}, '%') " +
            "OR d.specialty LIKE CONCAT('%', #{keyword}, '%') " +
            "OR dept.departmentName LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND d.doctorStatus = 1")
    List<Doctor> search(String keyword);
    
    @Select("SELECT * FROM doctor WHERE doctorPhone = #{phone} AND doctorStatus = 1")
    Doctor selectByPhone(@Param("phone") String phone);
    
    @Insert("INSERT INTO doctor (doctorName, doctorPassword, doctorGender, doctorPhone, doctorEmail, " +
            "departmentID, title, specialty, doctorIntro, doctorPhoto, registrationFee, doctorStatus) " +
            "VALUES (#{doctorName}, #{doctorPassword}, #{doctorGender}, #{doctorPhone}, #{doctorEmail}, " +
            "#{departmentID}, #{title}, #{specialty}, #{doctorIntro}, #{doctorPhoto}, #{registrationFee}, #{doctorStatus})")
    int insert(Doctor doctor);
    
    @Update("UPDATE doctor SET doctorName = #{doctorName}, doctorGender = #{doctorGender}, doctorPhone = #{doctorPhone}, " +
            "doctorEmail = #{doctorEmail}, departmentID = #{departmentID}, title = #{title}, specialty = #{specialty}, " +
            "doctorIntro = #{doctorIntro}, doctorPhoto = #{doctorPhoto}, registrationFee = #{registrationFee}, " +
            "doctorStatus = #{doctorStatus} WHERE doctorID = #{doctorID}")
    int update(Doctor doctor);
    
    @Delete("DELETE FROM doctor WHERE doctorID = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE doctor SET doctorStatus = #{status} WHERE doctorID = #{id}")
    int updateStatus(Long id, Integer status);
}
