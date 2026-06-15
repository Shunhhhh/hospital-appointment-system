package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Prescription;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 处方 Mapper
 */
@Mapper
public interface PrescriptionMapper {

    @Select("SELECT * FROM prescription WHERE recordID = #{recordId} ORDER BY createTime")
    List<Prescription> selectByRecordId(Long recordId);

    @Select("SELECT * FROM prescription WHERE visitID = #{visitId} ORDER BY createTime")
    List<Prescription> selectByVisitId(Long visitId);

    @Insert("INSERT INTO prescription (recordID, visitID, patientID, doctorID, " +
            "medicineName, medicineSpec, dosage, `usage`, frequency, course, " +
            "quantity, unit, price, totalPrice, remarks, prescriptionType, status, createTime) " +
            "VALUES (#{recordID}, #{visitID}, #{patientID}, #{doctorID}, " +
            "#{medicineName}, #{medicineSpec}, #{dosage}, #{usage}, #{frequency}, #{course}, " +
            "#{quantity}, #{unit}, #{price}, #{totalPrice}, #{remarks}, #{prescriptionType}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "prescriptionID")
    int insert(Prescription prescription);
}
