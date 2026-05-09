package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Department;
import com.hospital.appointment.service.DepartmentService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室管理控制器
 */
@RestController
@RequestMapping("/api/hospital/department")
@CrossOrigin(origins = "*")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    /**
     * 获取科室列表
     */
    @GetMapping("/list")
    public Result<List<Department>> getDepartmentList() {
        List<Department> list = departmentService.getDepartmentList();
        return Result.success(list);
    }
    
    /**
     * 获取科室详情
     */
    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        if (department != null) {
            return Result.success(department);
        }
        return Result.error("科室不存在");
    }
    
    /**
     * 根据科室类型获取科室列表
     */
    @GetMapping("/type/{type}")
    public Result<List<Department>> getDepartmentsByType(@PathVariable Integer type) {
        List<Department> list = departmentService.getDepartmentsByType(type);
        return Result.success(list);
    }
    
    /**
     * 添加科室
     */
    @PostMapping
    public Result<String> addDepartment(@RequestBody Department department) {
        boolean success = departmentService.addDepartment(department);
        if (success) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }
    
    /**
     * 更新科室
     */
    @PutMapping("/{id}")
    public Result<String> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        department.setDepartmentID(id);
        boolean success = departmentService.updateDepartment(department);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 删除科室
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDepartment(@PathVariable Long id) {
        boolean success = departmentService.deleteDepartment(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
    
    /**
     * 更新科室状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = departmentService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }
}
