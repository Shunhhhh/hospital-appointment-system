package com.hospital.appointment.controller;

import com.hospital.appointment.entity.Admin;
import com.hospital.appointment.service.HospitalAdminService;
import com.hospital.appointment.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/hospital/admin")
@CrossOrigin(origins = "*")
public class HospitalAdminController {

    @Autowired
    private HospitalAdminService hospitalAdminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<Admin> login(@RequestParam Integer adminID, @RequestParam String password) {
        Admin admin = hospitalAdminService.login(adminID, password);
        if (admin != null) {
            return Result.success(admin);
        }
        return Result.error("用户名或密码错误");
    }
}
