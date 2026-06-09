package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.CheckReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CheckReportMapper {

    @Select("SELECT cr.*, d.doctorName, dept.departmentName " +
            "FROM check_report cr " +
            "LEFT JOIN doctor d ON cr.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON cr.departmentID = dept.departmentID " +
            "WHERE cr.patientID = #{patientId} " +
            "ORDER BY cr.checkDate DESC")
    List<CheckReport> selectByPatient(@Param("patientId") Long patientId);

    @Select("<script>" +
            "SELECT cr.*, d.doctorName, dept.departmentName " +
            "FROM check_report cr " +
            "LEFT JOIN doctor d ON cr.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON cr.departmentID = dept.departmentID " +
            "WHERE cr.patientID = #{patientId} " +
            "<if test='reportType != null and reportType != \"\"'> AND cr.reportType = #{reportType} </if>" +
            "ORDER BY cr.checkDate DESC" +
            "</script>")
    List<CheckReport> selectByPatientAndType(@Param("patientId") Long patientId, @Param("reportType") String reportType);

    @Select("SELECT cr.*, d.doctorName, dept.departmentName " +
            "FROM check_report cr " +
            "LEFT JOIN doctor d ON cr.doctorID = d.doctorID " +
            "LEFT JOIN department dept ON cr.departmentID = dept.departmentID " +
            "WHERE cr.reportID = #{id}")
    CheckReport selectById(Long id);
}
