package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper {
    
    @Select("<script>" +
            "SELECT r.*, d.doctorName, dept.departmentName, " +
            "CASE WHEN r.isAnonymous = 0 THEN p.patientName ELSE '匿名用户' END AS patientName " +
            "FROM review r " +
            "LEFT JOIN doctor d ON r.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON r.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON r.patientID = p.patientID " +
            "WHERE r.doctorID = #{doctorId} AND r.reviewStatus = 2 " +
            "ORDER BY r.createTime DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Review> selectByDoctor(@Param("doctorId") Long doctorId, @Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT r.*, d.doctorName, dept.departmentName FROM review r " +
            "LEFT JOIN doctor d ON r.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON r.departmentID = dept.departmentID " +
            "WHERE r.patientID = #{patientId} ORDER BY r.createTime DESC")
    List<Review> selectByPatient(Long patientId);
    
    @Select("SELECT r.*, d.doctorName, dept.departmentName, p.patientName FROM review r " +
            "LEFT JOIN doctor d ON r.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON r.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON r.patientID = p.patientID " +
            "WHERE r.reviewID = #{id}")
    Review selectById(String id);
    
    @Select("SELECT AVG(overallRating) FROM review WHERE doctorID = #{doctorId} AND reviewStatus = 2")
    Double selectAverageRating(Long doctorId);
    
    @Select("SELECT r.*, d.doctorName, dept.departmentName, p.patientName FROM review r " +
            "LEFT JOIN doctor d ON r.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON r.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON r.patientID = p.patientID " +
            "WHERE r.reviewStatus = 1 ORDER BY r.createTime DESC")
    List<Review> selectPending();
    
    @Insert("INSERT INTO review (reviewID, patientID, doctorID, appointmentID, departmentID, visitID, " +
            "overallRating, attitudeRating, skillRating, environmentRating, reviewContent, reviewImages, " +
            "isAnonymous, reviewStatus, createTime) " +
            "VALUES (#{reviewID}, #{patientID}, #{doctorID}, #{appointmentID}, #{departmentID}, #{visitID}, " +
            "#{overallRating}, #{attitudeRating}, #{skillRating}, #{environmentRating}, #{reviewContent}, #{reviewImages}, " +
            "#{isAnonymous}, #{reviewStatus}, #{createTime})")
    int insert(Review review);
    
    @Update("UPDATE review SET replyContent = #{replyContent}, replyTime = #{replyTime} WHERE reviewID = #{reviewID}")
    int update(Review review);
    
    @Update("UPDATE review SET reviewStatus = #{status} WHERE reviewID = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") Integer status);
    
    @Delete("DELETE FROM review WHERE reviewID = #{id}")
    int deleteById(String id);
}
