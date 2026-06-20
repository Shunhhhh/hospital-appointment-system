package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HospitalNotificationMapper {

    @Select("SELECT * FROM notification ORDER BY sendTime DESC")
    List<Notification> selectAll();

    @Select("SELECT * FROM notification WHERE notificationID = #{id}")
    Notification selectById(String id);

    @Select("<script>" +
            "SELECT * FROM notification WHERE 1=1 " +
            "<if test='patientId != null'> AND patientID = #{patientId} </if>" +
            "<if test='type != null'> AND notificationType = #{type} </if>" +
            "ORDER BY sendTime DESC" +
            "</script>")
    List<Notification> selectByFilter(@Param("patientId") Long patientId, @Param("type") Integer type);

    @Select("SELECT * FROM notification ORDER BY sendTime DESC LIMIT #{limit}")
    List<Notification> selectLatest(@Param("limit") int limit);

    @Insert("INSERT INTO notification (notificationID, patientID, doctorID, adminID, notificationType, " +
            "title, notificationContent, relatedID, notificationStatus, sendTime, expireTime) " +
            "VALUES (#{notificationID}, #{patientID}, #{doctorID}, #{adminID}, #{notificationType}, " +
            "#{title}, #{notificationContent}, #{relatedID}, #{notificationStatus}, #{sendTime}, #{expireTime})")
    int insert(Notification notification);

    @Update("UPDATE notification SET notificationStatus = #{status}, readTime = NOW() WHERE notificationID = #{id}")
    int markRead(@Param("id") String id, @Param("status") Integer status);

    @Delete("DELETE FROM notification WHERE notificationID = #{id}")
    int deleteById(String id);
}
