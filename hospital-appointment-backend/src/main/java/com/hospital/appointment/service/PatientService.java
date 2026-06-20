package com.hospital.appointment.service;

import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 患者服务类
 */
@Service
public class PatientService {
    
    @Autowired
    private PatientMapper patientMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 患者注册
     */
    @Transactional
    public boolean register(Patient patient) {
        // 检查手机号和身份证是否已存在
        Patient existing = patientMapper.selectByPhone(patient.getPatientPhone());
        if (existing != null) {
            return false;
        }
        existing = patientMapper.selectByIdCard(patient.getIdCard());
        if (existing != null) {
            return false;
        }
        // 密码加密存储
        patient.setPatientPassword(passwordEncoder.encode(patient.getPatientPassword()));
        return patientMapper.insert(patient) > 0;
    }
    
    /**
     * 患者登录（BCrypt 密码验证）
     */
    public Patient login(String phone, String password) {
        Patient patient = patientMapper.login(phone);
        if (patient == null) {
            return null;
        }
        // 使用 BCrypt 验证密码
        if (passwordEncoder.matches(password, patient.getPatientPassword())) {
            return patient;
        }
        return null;
    }
    
    /**
     * 根据ID获取患者
     */
    public Patient getPatientById(Long id) {
        return patientMapper.selectById(id);
    }
    
    /**
     * 更新患者信息
     */
    public boolean updatePatient(Patient patient) {
        return patientMapper.update(patient) > 0;
    }
    
    /**
     * 修改密码
     */
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Patient patient = patientMapper.selectById(id);
        if (patient != null && passwordEncoder.matches(oldPassword, patient.getPatientPassword())) {
            patient.setPatientPassword(passwordEncoder.encode(newPassword));
            return patientMapper.update(patient) > 0;
        }
        return false;
    }
    
    /**
     * 检查是否在黑名单
     */
    public boolean isInBlacklist(Long id) {
        Patient patient = patientMapper.selectById(id);
        return patient != null && patient.getIsBlacklist() == 1;
    }
    
    /**
     * 获取信用积分
     */
    public int getCreditScore(Long id) {
        Patient patient = patientMapper.selectById(id);
        return patient != null ? patient.getCreditScore() : 0;
    }
}
