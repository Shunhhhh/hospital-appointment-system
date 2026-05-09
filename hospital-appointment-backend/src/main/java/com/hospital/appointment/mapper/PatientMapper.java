package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Patient;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 患者Mapper
 */
@Mapper
public interface PatientMapper {
    
    @Select("SELECT * FROM patient WHERE patientID = #{id}")
    Patient selectById(Long id);
    
    @Select("SELECT * FROM patient WHERE patientPhone = #{phone}")
    Patient selectByPhone(String phone);
    
    @Select("SELECT * FROM patient WHERE idCard = #{idCard}")
    Patient selectByIdCard(String idCard);
    
    @Select("SELECT * FROM patient WHERE patientPhone = #{phone}")
    Patient login(@Param("phone") String phone);
    
    @Insert("INSERT INTO patient (patientName, patientPassword, idCard, patientGender, patientBirthday, " +
            "patientPhone, patientEmail, patientAddress, medicalCardNo, insuranceType, insuranceNo, " +
            "allergyHistory, medicalHistory, emergencyContact, emergencyPhone, creditScore, noshowCount, isBlacklist, patientStatus) " +
            "VALUES (#{patientName}, #{patientPassword}, #{idCard}, #{patientGender}, #{patientBirthday}, " +
            "#{patientPhone}, #{patientEmail}, #{patientAddress}, #{medicalCardNo}, #{insuranceType}, #{insuranceNo}, " +
            "#{allergyHistory}, #{medicalHistory}, #{emergencyContact}, #{emergencyPhone}, 100, 0, 0, 1)")
    int insert(Patient patient);
    
    @Update("UPDATE patient SET patientName = #{patientName}, patientGender = #{patientGender}, " +
            "patientBirthday = #{patientBirthday}, patientPhone = #{patientPhone}, patientEmail = #{patientEmail}, " +
            "patientAddress = #{patientAddress}, insuranceType = #{insuranceType}, insuranceNo = #{insuranceNo}, " +
            "allergyHistory = #{allergyHistory}, medicalHistory = #{medicalHistory}, " +
            "emergencyContact = #{emergencyContact}, emergencyPhone = #{emergencyPhone} WHERE patientID = #{patientID}")
    int update(Patient patient);
    
    @Update("UPDATE patient SET creditScore = #{creditScore} WHERE patientID = #{patientID}")
    int updateCreditScore(@Param("patientID") Long patientID, @Param("creditScore") Integer creditScore);
    
    @Update("UPDATE patient SET isBlacklist = #{isBlacklist} WHERE patientID = #{patientID}")
    int updateBlacklist(@Param("patientID") Long patientID, @Param("isBlacklist") Integer isBlacklist);
}
