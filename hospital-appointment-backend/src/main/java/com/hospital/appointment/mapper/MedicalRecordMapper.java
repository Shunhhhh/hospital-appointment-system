package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.MedicalRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 门诊病历 Mapper
 */
@Mapper
public interface MedicalRecordMapper {

    @Select("SELECT mr.*, d.doctorName, dept.departmentName, p.patientName " +
            "FROM medical_record mr " +
            "LEFT JOIN doctor d ON mr.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON mr.patientID = p.patientID " +
            "WHERE mr.patientID = #{patientId} " +
            "ORDER BY mr.createTime DESC")
    List<MedicalRecord> selectByPatient(Long patientId);

    @Select("SELECT mr.*, d.doctorName, dept.departmentName, p.patientName " +
            "FROM medical_record mr " +
            "LEFT JOIN doctor d ON mr.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON d.departmentID = dept.departmentID " +
            "LEFT JOIN patient p ON mr.patientID = p.patientID " +
            "WHERE mr.recordID = #{recordId}")
    MedicalRecord selectById(Long recordId);

    @Select("SELECT mr.*, d.doctorName, p.patientName " +
            "FROM medical_record mr " +
            "LEFT JOIN doctor d ON mr.doctorID = d.doctorID " +
            "LEFT JOIN patient p ON mr.patientID = p.patientID " +
            "WHERE mr.appointmentID = #{appointmentId} " +
            "LIMIT 1")
    MedicalRecord selectByAppointmentId(String appointmentId);

    @Insert("INSERT INTO medical_record (visitID, appointmentID, patientID, doctorID, " +
            "chiefComplaint, presentIllness, pastHistory, allergyHistory, physicalExamination, " +
            "auxiliaryExamination, preliminaryDiagnosis, finalDiagnosis, treatmentPlan, " +
            "medicalAdvice, remarks, createTime, updateTime) " +
            "VALUES (#{visitID}, #{appointmentID}, #{patientID}, #{doctorID}, " +
            "#{chiefComplaint}, #{presentIllness}, #{pastHistory}, #{allergyHistory}, #{physicalExamination}, " +
            "#{auxiliaryExamination}, #{preliminaryDiagnosis}, #{finalDiagnosis}, #{treatmentPlan}, " +
            "#{medicalAdvice}, #{remarks}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "recordID")
    int insert(MedicalRecord record);

    @Update("UPDATE medical_record SET chiefComplaint = #{chiefComplaint}, " +
            "presentIllness = #{presentIllness}, pastHistory = #{pastHistory}, " +
            "allergyHistory = #{allergyHistory}, physicalExamination = #{physicalExamination}, " +
            "auxiliaryExamination = #{auxiliaryExamination}, preliminaryDiagnosis = #{preliminaryDiagnosis}, " +
            "finalDiagnosis = #{finalDiagnosis}, treatmentPlan = #{treatmentPlan}, " +
            "medicalAdvice = #{medicalAdvice}, remarks = #{remarks}, updateTime = #{updateTime} " +
            "WHERE recordID = #{recordID}")
    int update(MedicalRecord record);
}
