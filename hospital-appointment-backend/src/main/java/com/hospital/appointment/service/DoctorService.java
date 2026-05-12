package com.hospital.appointment.service;

import com.hospital.appointment.entity.Doctor;
import com.hospital.appointment.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 医生服务类
 */
@Service
public class DoctorService {
    
    @Autowired
    private DoctorMapper doctorMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 获取所有医生列表
     */
    public List<Doctor> getDoctorList() {
        return doctorMapper.selectAll();
    }
    
    /**
     * 根据ID获取医生
     */
    public Doctor getDoctorById(Long id) {
        return doctorMapper.selectById(id);
    }
    
    /**
     * 根据科室获取医生列表
     */
    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorMapper.selectByDepartment(departmentId);
    }
    
    /**
     * 根据职称获取医生列表
     */
    public List<Doctor> getDoctorsByTitle(String title) {
        return doctorMapper.selectByTitle(title);
    }
    
    /**
     * 搜索医生
     */
    public List<Doctor> searchDoctors(String keyword) {
        return doctorMapper.search(keyword);
    }
    
    /**
     * 医生登录（BCrypt 密码验证）
     */
    public Doctor login(String phone, String password) {
        Doctor doctor = doctorMapper.selectByPhone(phone);
        if (doctor == null) {
            return null;
        }
        // 使用 BCrypt 验证密码
        if (passwordEncoder.matches(password, doctor.getDoctorPassword())) {
            return doctor;
        }
        return null;
    }
    
    /**
     * 添加医生
     */
    public boolean addDoctor(Doctor doctor) {
        return doctorMapper.insert(doctor) > 0;
    }
    
    /**
     * 更新医生信息
     */
    public boolean updateDoctor(Doctor doctor) {
        return doctorMapper.update(doctor) > 0;
    }
    
    /**
     * 删除医生
     */
    public boolean deleteDoctor(Long id) {
        return doctorMapper.deleteById(id) > 0;
    }
    
    /**
     * 更新医生状态
     */
    public boolean updateStatus(Long id, Integer status) {
        return doctorMapper.updateStatus(id, status) > 0;
    }
}
