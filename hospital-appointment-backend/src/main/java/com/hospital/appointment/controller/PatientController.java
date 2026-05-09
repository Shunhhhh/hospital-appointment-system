package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.service.PatientService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者控制器
 * 改造自 studentController
 */
@RestController
@RequestMapping("/api/hospital/patient")
@CrossOrigin(origins = "*")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    /**
     * 患者注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody Patient patient) {
        boolean success = patientService.register(patient);
        if (success) {
            return Result.success("注册成功");
        }
        return Result.error("注册失败，该手机号或身份证号已注册");
    }
    
    /**
     * 患者登录
     */
    @PostMapping("/login")
    public Result<Patient> login(@RequestParam String phone, @RequestParam String password) {
        Patient patient = patientService.login(phone, password);
        if (patient != null) {
            return Result.success(patient);
        }
        return Result.error("用户名或密码错误");
    }
    
    /**
     * 获取患者信息
     */
    @GetMapping("/{id}")
    public Result<Patient> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        if (patient != null) {
            return Result.success(patient);
        }
        return Result.error("患者不存在");
    }
    
    /**
     * 更新患者信息
     */
    @PutMapping("/{id}")
    public Result<String> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        patient.setPatientID(id);
        boolean success = patientService.updatePatient(patient);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/{id}/password")
    public Result<String> changePassword(@PathVariable Long id, 
                                         @RequestParam String oldPassword, 
                                         @RequestParam String newPassword) {
        boolean success = patientService.changePassword(id, oldPassword, newPassword);
        if (success) {
            return Result.success("密码修改成功");
        }
        return Result.error("原密码错误");
    }
    
    /**
     * 检查是否在黑名单
     */
    @GetMapping("/{id}/blacklist")
    public Result<Boolean> isInBlacklist(@PathVariable Long id) {
        boolean isBlacklist = patientService.isInBlacklist(id);
        return Result.success(isBlacklist);
    }
    
    /**
     * 获取患者信用积分
     */
    @GetMapping("/{id}/credit")
    public Result<Integer> getCreditScore(@PathVariable Long id) {
        int creditScore = patientService.getCreditScore(id);
        return Result.success(creditScore);
    }
}
