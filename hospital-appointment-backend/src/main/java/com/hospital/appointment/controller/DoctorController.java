package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Doctor;
import com.hospital.appointment.service.DoctorService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生管理控制器
 */
@RestController
@RequestMapping("/api/hospital/doctor")
@CrossOrigin(origins = "*")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    /**
     * 获取医生列表
     */
    @GetMapping("/list")
    public Result<List<Doctor>> getDoctorList() {
        List<Doctor> list = doctorService.getDoctorList();
        return Result.success(list);
    }
    
    /**
     * 获取医生详情
     */
    @GetMapping("/{id}")
    public Result<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor != null) {
            return Result.success(doctor);
        }
        return Result.error("医生不存在");
    }
    
    /**
     * 根据科室获取医生列表
     */
    @GetMapping("/department/{departmentId}")
    public Result<List<Doctor>> getDoctorsByDepartment(@PathVariable Long departmentId) {
        List<Doctor> list = doctorService.getDoctorsByDepartment(departmentId);
        return Result.success(list);
    }
    
    /**
     * 根据职称获取医生列表
     */
    @GetMapping("/title/{title}")
    public Result<List<Doctor>> getDoctorsByTitle(@PathVariable String title) {
        List<Doctor> list = doctorService.getDoctorsByTitle(title);
        return Result.success(list);
    }
    
    /**
     * 搜索医生
     */
    @GetMapping("/search")
    public Result<List<Doctor>> searchDoctors(@RequestParam String keyword) {
        List<Doctor> list = doctorService.searchDoctors(keyword);
        return Result.success(list);
    }
    
    /**
     * 医生登录
     */
    @PostMapping("/login")
    public Result<Doctor> doctorLogin(@RequestParam String phone, @RequestParam String password) {
        Doctor doctor = doctorService.login(phone, password);
        if (doctor != null) {
            return Result.success(doctor);
        }
        return Result.error("用户名或密码错误");
    }
    
    /**
     * 添加医生
     */
    @PostMapping
    public Result<String> addDoctor(@RequestBody Doctor doctor) {
        boolean success = doctorService.addDoctor(doctor);
        if (success) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }
    
    /**
     * 更新医生信息
     */
    @PutMapping("/{id}")
    public Result<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctor.setDoctorID(id);
        boolean success = doctorService.updateDoctor(doctor);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 删除医生
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDoctor(@PathVariable Long id) {
        boolean success = doctorService.deleteDoctor(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
    
    /**
     * 更新医生状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = doctorService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }
}
