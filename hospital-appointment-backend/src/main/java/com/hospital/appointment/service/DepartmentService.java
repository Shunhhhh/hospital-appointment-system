package com.hospital.appointment.service;

import com.hospital.appointment.entity.Department;
import com.hospital.appointment.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 科室服务类
 */
@Service
public class DepartmentService {
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    /**
     * 获取所有科室列表
     */
    public List<Department> getDepartmentList() {
        return departmentMapper.selectAll();
    }
    
    /**
     * 根据ID获取科室
     */
    public Department getDepartmentById(Long id) {
        return departmentMapper.selectById(id);
    }
    
    /**
     * 根据科室类型获取科室列表
     */
    public List<Department> getDepartmentsByType(Integer type) {
        return departmentMapper.selectByType(type);
    }
    
    /**
     * 添加科室
     */
    public boolean addDepartment(Department department) {
        return departmentMapper.insert(department) > 0;
    }
    
    /**
     * 更新科室
     */
    public boolean updateDepartment(Department department) {
        return departmentMapper.update(department) > 0;
    }
    
    /**
     * 删除科室
     */
    public boolean deleteDepartment(Long id) {
        return departmentMapper.deleteById(id) > 0;
    }
    
    /**
     * 更新科室状态
     */
    public boolean updateStatus(Long id, Integer status) {
        return departmentMapper.updateStatus(id, status) > 0;
    }
}
