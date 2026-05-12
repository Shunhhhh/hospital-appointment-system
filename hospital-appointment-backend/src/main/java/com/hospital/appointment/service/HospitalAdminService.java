package com.hospital.appointment.service;

import com.hospital.appointment.entity.Admin;
import com.hospital.appointment.mapper.HospitalAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 管理员服务类
 */
@Service
public class HospitalAdminService {

    @Autowired
    private HospitalAdminMapper hospitalAdminMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 管理员登录（BCrypt 密码验证）
     */
    public Admin login(Integer adminID, String password) {
        Admin admin = hospitalAdminMapper.selectById(adminID);
        if (admin == null) {
            return null;
        }
        if (passwordEncoder.matches(password, admin.getAdminPassword())) {
            return admin;
        }
        return null;
    }
}
