package com.hospital.appointment.mapper;

import com.hospital.appointment.entity.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 科室Mapper
 */
@Mapper
public interface DepartmentMapper {
    
    @Select("SELECT * FROM department ORDER BY displayOrder ASC")
    List<Department> selectAll();
    
    @Select("SELECT * FROM department WHERE departmentID = #{id}")
    Department selectById(Long id);
    
    @Select("SELECT * FROM department WHERE departmentType = #{type} ORDER BY displayOrder ASC")
    List<Department> selectByType(Integer type);
    
    @Insert("INSERT INTO department (departmentName, departmentType, departmentLocation, departmentDesc, departmentIcon, departmentStatus, displayOrder) " +
            "VALUES (#{departmentName}, #{departmentType}, #{departmentLocation}, #{departmentDesc}, #{departmentIcon}, #{departmentStatus}, #{displayOrder})")
    int insert(Department department);
    
    @Update("UPDATE department SET departmentName = #{departmentName}, departmentType = #{departmentType}, " +
            "departmentLocation = #{departmentLocation}, departmentDesc = #{departmentDesc}, departmentIcon = #{departmentIcon}, " +
            "departmentStatus = #{departmentStatus}, displayOrder = #{displayOrder} WHERE departmentID = #{departmentID}")
    int update(Department department);
    
    @Delete("DELETE FROM department WHERE departmentID = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE department SET departmentStatus = #{status} WHERE departmentID = #{id}")
    int updateStatus(Long id, Integer status);
}
